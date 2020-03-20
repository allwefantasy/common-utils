package tech.mlsql.common.utils.io

import java.io.{IOException, InputStream, OutputStream}

import tech.mlsql.common.utils.base.TryTool

class RedirectThread(
                      in: InputStream,
                      out: OutputStream,
                      name: String,
                      propagateEof: Boolean = false)
  extends Thread(name) {

  setDaemon(true)

  override def run() {
    scala.util.control.Exception.ignoring(classOf[IOException]) {
      // FIXME: We copy the stream on the level of bytes to avoid encoding problems.
      TryTool.tryWithSafeFinally {
        val buf = new Array[Byte](1024)
        var len = in.read(buf)
        while (len != -1) {
          out.write(buf, 0, len)
          out.flush()
          len = in.read(buf)
        }
      } {
        if (propagateEof) {
          out.close()
        }
      }
    }
  }
}

trait RedirectStreams {

  def setConf(conf: Map[String, String]): Unit

  def conf: Map[String, String]

  def stdOut(stdOut: InputStream): Unit

  def stdErr(stdErr: InputStream): Unit

  def setTarget(target: OutputStream): Unit
}
