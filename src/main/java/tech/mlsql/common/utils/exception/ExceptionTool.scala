package tech.mlsql.common.utils.exception

import java.io.{PrintWriter, StringWriter}

import tech.mlsql.common.utils.log.Logging

import scala.collection.mutable.ArrayBuffer
import scala.util.control.{ControlThrowable, NonFatal}

/**
 * 19/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
object ExceptionTool extends Logging {


  /** Returns true if the given exception was fatal. See docs for scala.util.control.NonFatal. */
  def isFatalError(e: Throwable): Boolean = {
    e match {
      case NonFatal(_) |
           _: InterruptedException |
           _: NotImplementedError |
           _: ControlThrowable |
           _: LinkageError =>
        false
      case _ =>
        true
    }
  }

  /**
   * Execute the given block, logging and re-throwing any uncaught exception.
   * This is particularly useful for wrapping code that runs in a thread, to ensure
   * that exceptions are printed, and to avoid having to catch Throwable.
   */

  def logUncaughtExceptions[T](f: => T): T = {
    try {
      f
    } catch {
      case ct: ControlThrowable =>
        throw ct
      case t: Throwable =>
        logError(s"Uncaught exception in thread ${Thread.currentThread().getName}", t)
        throw t
    }
  }

  def format_exception(e: Exception, callback: String => String = (str: String) => str) = {
    (e.toString.split("\n") ++ e.getStackTrace.map(f => f.toString)).map(f => callback(f)).toSeq.mkString("\n")
  }

  def format_throwable(e: Throwable, skipPrefix: Boolean = false, callback: (String,Boolean) => String = (str: String,skipPrefix:Boolean) => str) = {
    (e.toString.split("\n") ++ e.getStackTrace.map(f => f.toString)).map(f => callback(f, skipPrefix)).toSeq.mkString("\n")
  }

  def format_cause(e: Exception) = {
    var cause = e.asInstanceOf[Throwable]
    while (cause.getCause != null) {
      cause = cause.getCause
    }
    format_throwable(cause)
  }

  def format_full_exception(buffer: ArrayBuffer[String], e: Exception, skipPrefix: Boolean = true) = {
    var cause = e.asInstanceOf[Throwable]
    buffer += format_throwable(cause, skipPrefix)
    while (cause.getCause != null) {
      cause = cause.getCause
      buffer += "caused by：\n" + format_throwable(cause, skipPrefix)
    }

  }

  /**
   * Return a nice string representation of the exception. It will call "printStackTrace" to
   * recursively generate the stack trace including the exception and its causes.
   */
  def exceptionString(e: Throwable): String = {
    if (e == null) {
      ""
    } else {
      // Use e.printStackTrace here because e.getStackTrace doesn't include the cause
      val stringWriter = new StringWriter()
      e.printStackTrace(new PrintWriter(stringWriter))
      stringWriter.toString
    }
  }


}
