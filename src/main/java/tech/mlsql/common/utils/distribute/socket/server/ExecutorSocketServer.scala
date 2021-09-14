package tech.mlsql.common.utils.distribute.socket.server

import java.io.{DataInputStream, DataOutputStream}
import java.net.{InetAddress, InetSocketAddress, ServerSocket, Socket}
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

import tech.mlsql.common.utils.log.Logging
import tech.mlsql.common.utils.serder.json.JsonUtils

object SocketServerInExecutor extends Logging {

  val threadPool = Executors.newFixedThreadPool(100)


  def setupOneConnectionServer(host: String, threadName: String)
                              (func: Socket => Unit): (ServerSocket, String, Int) = {

    val serverSocket: ServerSocket = new ServerSocket(0, 1, InetAddress.getByName(host))
    // Close the socket if no connection in 5 min
    serverSocket.setSoTimeout(1000 * 60 * 5)
    new Thread(threadName) {
      setDaemon(true)

      override def run(): Unit = {
        var sock: Socket = null
        try {
          sock = serverSocket.accept()
          func(sock)
        } catch {
          case e: Exception =>
            logInfo(s"The server ${serverSocket} is closing the socket ${sock} connection")
        } finally {
          JavaUtils.closeQuietly(serverSocket)
          JavaUtils.closeQuietly(sock)
        }
      }
    }.start()

    (serverSocket, host, serverSocket.getLocalPort)
  }


  def setupMultiConnectionServer[T](host: String, taskContextRef: AtomicReference[T], threadName: String)
                                   (func: Socket => Unit)(completeCallback: () => Unit): (ServerSocket, String, Int) = {


    //    val host = if (SparkEnv.get == null) {
    //      //When SparkEnv.get is null, the program may run in a test
    //      //So return local address would be ok.
    //      "127.0.0.1"
    //    } else {
    //      SparkEnv.get.rpcEnv.address.host
    //    }
    val serverSocket: ServerSocket = new ServerSocket(0, 10000, InetAddress.getByName(host))
    // throw exception if  the socket server have no connection in 5 min
    // then we will close the serverSocket
    //serverSocket.setSoTimeout(1000 * 60 * 5)

    new Thread(threadName) {
      setDaemon(true)

      override def run(): Unit = {
        try {
          /**
           * Since we will start this BinLogSocketServerInExecutor in spark task, so when we kill the task,
           * The taskContext should also be null
           */
          while (taskContextRef.get() != null) {
            val socket = serverSocket.accept()
            threadPool.submit(new Runnable {
              override def run(): Unit = {
                try {
                  logInfo("Received connection from" + socket)
                  func(socket)
                } catch {
                  case e: Exception =>
                    logInfo(s"The server ${serverSocket} is closing the socket ${socket} connection", e)
                } finally {
                  JavaUtils.closeQuietly(socket)
                }
              }
            })
          }
          completeCallback()
          JavaUtils.closeQuietly(serverSocket)
        }
        catch {
          case e: Exception => logInfo("", e)
        }

      }
    }.start()

    (serverSocket, host, serverSocket.getLocalPort)
  }

  def reportHostAndPort(tempSocketServerHost: String, tempSocketServerPort: Int, rhap: ReportHostAndPort) = {
    val socket = new Socket()
    socket.connect(new InetSocketAddress(tempSocketServerHost, tempSocketServerPort))
    val din = new DataInputStream(socket.getInputStream)
    val dout = new DataOutputStream(socket.getOutputStream)
    val client = new SocketServerSerDer[ReportSingleAction, ReportSingleAction]() {}
    try {

      client.sendRequest(dout, rhap)
      client.readRequest(din) match {
        case a: ReportHostAndPort =>
      }
    } finally {
      try {
        socket.close()
      } catch {
        case e: Exception => logError("fail to close reportHostAndPort socket", e)
      }

    }
  }
}

abstract class SocketServerInExecutor[T](taskContextRef: AtomicReference[T], threadName: String) {

  val (_server, _host, _port) = SocketServerInExecutor.setupMultiConnectionServer(host, taskContextRef, threadName) { sock =>
    handleConnection(sock)
  }(() => {
    close
  })

  def handleConnection(sock: Socket): Unit

  def close: Unit

  def host: String
}


abstract class Request[SINGLE: Manifest] {
  def wrap: SINGLE

  def json: String = JsonUtils.toJson(wrap)
}

abstract class Response[SINGLE: Manifest] {
  def wrap: SINGLE

  def json: String = JsonUtils.toJson(wrap)
}


abstract class SocketServerSerDer[IN: Manifest, OUT: Manifest] {
  def readRequest(dIn: DataInputStream) = {
    val length = dIn.readInt()
    val bytes = new Array[Byte](length)
    dIn.readFully(bytes, 0, length)
    val response = JsonUtils.fromJson[IN](new String(bytes, StandardCharsets.UTF_8)).
      asInstanceOf[ {def unwrap: Request[_]}].unwrap
    response
  }

  def sendRequest(dOut: DataOutputStream, request: Request[IN]) = {
    val bytes = request.json.getBytes(StandardCharsets.UTF_8)
    dOut.writeInt(bytes.length)
    dOut.write(bytes)
    dOut.flush()
  }

  def sendResponse(dOut: DataOutputStream, response: Response[OUT]) = {
    val bytes = response.json.getBytes(StandardCharsets.UTF_8)
    dOut.writeInt(bytes.length)
    dOut.write(bytes)
    dOut.flush()
  }

  def readResponse(dIn: DataInputStream) = {
    val length = dIn.readInt()
    val bytes = new Array[Byte](length)
    dIn.readFully(bytes, 0, length)
    val response = JsonUtils.fromJson[OUT](new String(bytes, StandardCharsets.UTF_8)).
      asInstanceOf[ {def unwrap: Response[_]}].unwrap
    response
  }

  def sendMarkRequest(dOut: DataOutputStream, mark: Int): Unit = {
    dOut.writeInt(mark)
    dOut.flush()
  }

  def readIterator(dIn: DataInputStream): Iterator[Response[_]] = {

    new Iterator[Response[_]]() {
      private var dataOrMark = SocketIteratorMark.HEAD
      private var nextObj: Response[_] = _
      private var eos = false

      override def hasNext: Boolean = nextObj != null || {
        if (!eos) {
          dataOrMark = dIn.readInt

          def next(mark: Int): Boolean = {
            mark match {
              case SocketIteratorMark.END =>
                eos = true // eos=true !!!
                false // 结束位
              case SocketIteratorMark.HEAD => // 开始位 需要继续读一位
                dataOrMark = dIn.readInt // 长度位 or end
                next(dataOrMark)
              case _ =>
                val bytes = new Array[Byte](dataOrMark)
                dIn.readFully(bytes, 0, dataOrMark)
                val response = JsonUtils.fromJson[OUT](new String(bytes, StandardCharsets.UTF_8)).
                  asInstanceOf[ {def unwrap: Response[_]}].unwrap
                nextObj = response
                true
            }
          }

          next(dataOrMark)
        } else {
          false
        }
      }

      override def next(): Response[_] = {
        if (hasNext) {
          val obj = nextObj
          nextObj = null.asInstanceOf[Response[_]]
          obj
        } else {
          Iterator.empty.next()
        }
      }
    }
  }
}

case class ReportHostAndPort(host: String, port: Int) extends Request[ReportSingleAction] {
  override def wrap: ReportSingleAction = ReportSingleAction(reportHostAndPort = this)
}

abstract class TempSocketServerInDriver(context: AtomicReference[ReportHostAndPort]) extends SocketServerSerDer[ReportSingleAction, ReportSingleAction] with Logging {
  val (_server, _host, _port) = SocketServerInExecutor.setupOneConnectionServer(host, "driver-temp-socket-server") { sock =>
    handleConnection(sock)
  }

  def handleConnection(socket: Socket): Unit = {
    val dIn = new DataInputStream(socket.getInputStream)
    val req = readRequest(dIn).asInstanceOf[ReportHostAndPort]
    context.set(req)
  }

  def host: String
}

case class ReportSingleAction(reportHostAndPort: ReportHostAndPort = null) {
  def unwrap: Request[_] = {
    if (reportHostAndPort != null) return reportHostAndPort
    else null
  }
}

object SocketIteratorMark {
  val HEAD = -2 // 交付开始
  val END = -1 // 交付结束
}







