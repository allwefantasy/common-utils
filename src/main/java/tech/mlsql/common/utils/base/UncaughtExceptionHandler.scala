package tech.mlsql.common.utils.base

import tech.mlsql.common.utils.exception.AppFatalException
import tech.mlsql.common.utils.hook.JVMShutdownHookManager
import tech.mlsql.common.utils.log.Logging

/**
 * 18/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
class UncaughtExceptionHandler(val exitOnUncaughtException: Boolean = true)
  extends Thread.UncaughtExceptionHandler with Logging {

  override def uncaughtException(thread: Thread, exception: Throwable) {
    try {
      // Make it explicit that uncaught exceptions are thrown when container is shutting down.
      // It will help users when they analyze the executor logs
      val inShutdownMsg = if (JVMShutdownHookManager.inShutdown()) "[Container in shutdown] " else ""
      val errMsg = "Uncaught exception in thread "
      logError(inShutdownMsg + errMsg + thread, exception)

      // We may have been called from a shutdown hook. If so, we must not call System.exit().
      // (If we do, we will deadlock.)
      if (!JVMShutdownHookManager.inShutdown()) {
        exception match {
          case _: OutOfMemoryError =>
            System.exit(AppExitCode.OOM)
          case e: AppFatalException if e.throwable.isInstanceOf[OutOfMemoryError] =>
            // SPARK-24294: This is defensive code, in case that SparkFatalException is
            // misused and uncaught.
            System.exit(AppExitCode.OOM)
          case _ if exitOnUncaughtException =>
            System.exit(AppExitCode.UNCAUGHT_EXCEPTION)
        }
      }
    } catch {
      case oom: OutOfMemoryError => Runtime.getRuntime.halt(AppExitCode.OOM)
      case t: Throwable => Runtime.getRuntime.halt(AppExitCode.UNCAUGHT_EXCEPTION_TWICE)
    }
  }

  def uncaughtException(exception: Throwable) {
    uncaughtException(Thread.currentThread(), exception)
  }
}
