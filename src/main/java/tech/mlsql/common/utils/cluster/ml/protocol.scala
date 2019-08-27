package tech.mlsql.common.utils.cluster.ml

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import tech.mlsql.common.utils.distribute.socket.server.{Request, Response, SocketServerInExecutor, SocketServerSerDer}
import tech.mlsql.common.utils.log.Logging

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

abstract class MLDriver[T](taskContextRef: AtomicReference[T]) extends SocketServerInExecutor[T](taskContextRef, "tf-socket-server-in-driver") with Logging {

  private val connections = new ArrayBuffer[Socket]()
  @volatile private var markClose: AtomicBoolean = new AtomicBoolean(false)
  val client = new MLClient()

  val workers = new java.util.concurrent.CopyOnWriteArraySet[WorkerInfo]()

  override def close() = {
    // make sure we only close once
    if (markClose.compareAndSet(false, true)) {
      logInfo(s"Shutdown ${host}. This may caused by the task is killed.")
    }
  }

  override def handleConnection(socket: Socket): Unit = {
    connections += socket
    socket.setKeepAlive(true)
    val dIn = new DataInputStream(socket.getInputStream)
    val dOut = new DataOutputStream(socket.getOutputStream)

    while (true) {
      client.readRequest(dIn) match {
        case ReportToMasterRequest(host, port, jobName, taskIndex, isPs) =>
          workers.add(WorkerInfo(host, port, jobName, taskIndex, isPs, false, false))
          client.sendResponse(dOut, ReportToMasterResponse())
        case ClusterSpecRequest() =>
          client.sendResponse(dOut, ClusterSpecResponse(workers.asScala.toList))

        case JobStatusRequest(jobName, taskIndex, done, success, shouldUpdate) =>
          synchronized {
            if (shouldUpdate) {
              val tempWorker = workers.asScala.filter(f => f.jobName == jobName && f.taskIndex == taskIndex).head
              workers.remove(tempWorker)
              workers.add(tempWorker.copy(done = done, success = success))
            }
            logInfo("update jobstat:" + JobStatusResponse(workers.asScala.toList))
            client.sendResponse(dOut, JobStatusResponse(workers.asScala.toList))
          }
      }
    }

  }
}


abstract class MLWorkerProxy(masterHost: String, masterPort: Int) extends Logging {
  val masterClient = new MLClient()
  val socket = new Socket(masterHost, masterPort)
  val driverOutputStream = new DataOutputStream(socket.getOutputStream)
  val driverInputStream = new DataInputStream(socket.getInputStream)


  def close = {
    socket.close()
  }

  def reportToMaster(rtr: ReportToMasterRequest) = {
    masterClient.sendRequest(driverOutputStream, rtr)
    masterClient.readResponse(driverInputStream).asInstanceOf[ReportToMasterResponse]
  }

  def fetchClusterSpec(): ClusterSpecResponse = {
    masterClient.sendRequest(driverOutputStream, ClusterSpecRequest())
    masterClient.readResponse(driverInputStream).asInstanceOf[ClusterSpecResponse]
  }

  def workerTaskName(): (WorkerInfo) => String

  def parameterServerTaskName(): (WorkerInfo) => String

  def reportSuccess(jsr: JobStatusRequest) = {
    masterClient.sendRequest(driverOutputStream, jsr)
    masterClient.readResponse(driverInputStream).asInstanceOf[JobStatusResponse]
  }

  def reportFails(jsr: JobStatusRequest) = {
    masterClient.sendRequest(driverOutputStream, jsr)
    masterClient.readResponse(driverInputStream).asInstanceOf[JobStatusResponse]
  }

  def waitDoneOrFail(targetWorkers: Int, curentJSR: JobStatusRequest, timeout: Long = 24 * 60 * 60 * 1000) = {
    var shouldWait = true

    def fetchClusterStatusFromMaster = {
      masterClient.sendRequest(driverOutputStream, curentJSR)
      masterClient.readResponse(driverInputStream).asInstanceOf[JobStatusResponse]
    }

    // PS should wait until
    // all worker finish their jobs.
    // PS python worker use thread.join to keep it alive .
    while (shouldWait) {
      val response = fetchClusterStatusFromMaster
      val workSize = response.jobStatus.filter(f => f.done && !f.isPs).size
      if (workSize == targetWorkers) {
        shouldWait = false
      }
      try {
        Thread.sleep(10000)
      } catch {
        case e: Exception =>
          shouldWait = false
      }
      logInfo(
        s"""
           |PS check worker is all finished.
           |targetSize:  ${targetWorkers}
           |currentSize: ${workSize}
            """.stripMargin)
    }


  }

  def waitOthers(expectedServers: Int, timeout: Long = 10 * 60 * 1000) = {
    // wait until all worker/ps have been registered
    var waitCount = 0
    val maxWaitCount = 100
    var noWait = false
    val clusterSpecRef = new AtomicReference[ClusterSpecResponse]()
    while (!noWait && waitCount < maxWaitCount) {
      val response = fetchClusterSpec()
      val totalRegistered = response.workerTaskNames(workerTaskName()).size + response.parameterServerTaskNames(parameterServerTaskName()).size
      logInfo(
        s"""
           |
             |----------------------------------
           |Waiting all worker/ps started
           |__________________________________
           |Wait times: ${waitCount} times.
           |Target: ${expectedServers}
           |totalRegistered: ${totalRegistered}
           |
           |PS: ${response.parameterServers}
           |Workers: ${response.workers}
           |
           """.stripMargin)
      if (totalRegistered == expectedServers) {
        clusterSpecRef.set(response)
        noWait = true
      } else {
        Thread.sleep(5000)
        waitCount += 1
      }
    }
  }
}

class MLClient extends SocketServerSerDer[MLSocketRequest, MLSocketResponse]

case class ClusterSpecRequest() extends Request[MLSocketRequest] {
  override def wrap = MLSocketRequest(clusterSpecRequest = this)
}

case class ClusterSpecResponse(workerInfoList: List[WorkerInfo]) extends Response[MLSocketResponse] {
  override def wrap = MLSocketResponse(clusterSpecResponse = this)

  def workers = {
    workerInfoList.filter(f => !f.isPs)
  }

  def parameterServers = {
    workerInfoList.filter(f => f.isPs)
  }

  def workerTaskNames(c: (WorkerInfo) => String) = {
    //s"/job:worker/task:${f.taskIndex}"
    workers.filter(f => !f.isPs).map(f => c(f))
  }

  def parameterServerTaskNames(c: (WorkerInfo) => String) = {
    // s"/job:ps/task:${f.taskIndex}"
    parameterServers.map(f => c(f))
  }
}

case class WorkerInfo(host: String, port: Int, jobName: String, taskIndex: Int, isPs: Boolean, done: Boolean, success: Boolean)

case class ReportToMasterRequest(host: String, port: Int, jobName: String, taskIndex: Int, isPs: Boolean) extends Request[MLSocketRequest] {
  override def wrap = MLSocketRequest(reportToMasterRequest = this)
}

case class ReportToMasterResponse() extends Response[MLSocketResponse] {
  override def wrap = MLSocketResponse(reportToMasterResponse = this)
}

case class JobStatusRequest(jobName: String, taskIndex: Int, done: Boolean, succcess: Boolean, shouldUpdate: Boolean) extends Request[MLSocketRequest] {
  override def wrap = MLSocketRequest(jobStatusRequest = this)
}


case class JobStatusResponse(jobStatus: List[WorkerInfo]) extends Response[MLSocketResponse] {
  override def wrap = MLSocketResponse(jobStatusResponse = this)
}

case class NoneOpResp() extends Response[MLSocketResponse] {
  override def wrap = MLSocketResponse(noneOpResp = this)
}

case class NoneOpReq() extends Request[MLSocketRequest] {
  override def wrap = MLSocketRequest(noneOpReq = this)
}


case class MLSocketResponse(clusterSpecResponse: ClusterSpecResponse = null,
                            reportToMasterResponse: ReportToMasterResponse = null,
                            jobStatusResponse: JobStatusResponse = null,
                            noneOpResp: NoneOpResp = null
                           ) {
  def unwrap: Response[_] = {
    if (clusterSpecResponse != null) clusterSpecResponse
    else if (reportToMasterResponse != null) reportToMasterResponse
    else if (jobStatusResponse != null) jobStatusResponse
    else if (noneOpResp != null) noneOpResp

    else null
  }
}

case class MLSocketRequest(clusterSpecRequest: ClusterSpecRequest = null,
                           reportToMasterRequest: ReportToMasterRequest = null,
                           jobStatusRequest: JobStatusRequest = null,
                           noneOpReq: NoneOpReq = null
                          ) {
  def unwrap: Request[_] = {
    if (clusterSpecRequest != null) clusterSpecRequest
    else if (reportToMasterRequest != null) reportToMasterRequest
    else if (jobStatusRequest != null) jobStatusRequest
    else if (noneOpReq != null) noneOpReq
    else null
  }
}




