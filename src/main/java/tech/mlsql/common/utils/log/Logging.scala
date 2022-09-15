package tech.mlsql.common.utils.log

/**
  * 2019-08-12 WilliamZhu(allwefantasy@gmail.com)
  */
import org.apache.logging.log4j.core.{ Logger => Log4jLogger}
import org.apache.logging.log4j.{Level, LogManager}
import org.apache.logging.log4j.core.config.DefaultConfiguration
import org.apache.logging.log4j.core.LoggerContext
import org.slf4j.impl.StaticLoggerBinder
import org.slf4j.{Logger, LoggerFactory}
import tech.mlsql.common.utils.Utils

/**
 * Utility trait for classes that want to log data. Creates a SLF4J logger for the class and allows
 * logging messages at different levels using methods that only evaluate parameters lazily if the
 * log level is enabled.
 */
trait Logging {

  // Make the log field transient so that objects with Logging can
  // be serialized and used on another machine
  @transient private var log_ : Logger = null

  // Method to get the logger name for this object
  protected def logName = {
    // Ignore trailing $'s in the class names for Scala objects
    this.getClass.getName.stripSuffix("$")
  }

  // Method to get or create the logger for this object
  protected def log: Logger = {
    if (log_ == null) {
      initializeLogIfNecessary(false)
      log_ = LoggerFactory.getLogger(logName)
    }
    log_
  }

  // Log methods that take only a String
  protected def logInfo(msg: => String) {
    if (log.isInfoEnabled) log.info(msg)
  }

  protected def logDebug(msg: => String) {
    if (log.isDebugEnabled) log.debug(msg)
  }

  protected def logTrace(msg: => String) {
    if (log.isTraceEnabled) log.trace(msg)
  }

  protected def logWarning(msg: => String) {
    if (log.isWarnEnabled) log.warn(msg)
  }

  protected def logError(msg: => String) {
    if (log.isErrorEnabled) log.error(msg)
  }

  // Log methods that take Throwables (Exceptions/Errors) too
  protected def logInfo(msg: => String, throwable: Throwable) {
    if (log.isInfoEnabled) log.info(msg, throwable)
  }

  protected def logDebug(msg: => String, throwable: Throwable) {
    if (log.isDebugEnabled) log.debug(msg, throwable)
  }

  protected def logTrace(msg: => String, throwable: Throwable) {
    if (log.isTraceEnabled) log.trace(msg, throwable)
  }

  protected def logWarning(msg: => String, throwable: Throwable) {
    if (log.isWarnEnabled) log.warn(msg, throwable)
  }

  protected def logError(msg: => String, throwable: Throwable) {
    if (log.isErrorEnabled) log.error(msg, throwable)
  }

  protected def isTraceEnabled(): Boolean = {
    log.isTraceEnabled
  }

  protected def initializeLogIfNecessary(isInterpreter: Boolean): Unit = {
    if (!Logging.initialized) {
      Logging.initLock.synchronized {
        if (!Logging.initialized) {
          initializeLogging(isInterpreter, false)
        }
      }
    }
  }

  private def initializeLogging(isInterpreter: Boolean, silent: Boolean): Unit = {
    if (Logging.isLog4j2()) {
      val rootLogger = LogManager.getRootLogger.asInstanceOf[Log4jLogger]
      // If Log4j 2 is used but is initialized by default configuration,
      // load a default properties file
      // scalastyle:off println
      if (Logging.islog4j2DefaultConfigured()) {
        Logging.defaultSparkLog4jConfig = true
        val defaultLogProps = "org/apache/spark/log4j2-defaults.properties"
        Option(Utils.getSparkClassLoader.getResource(defaultLogProps)) match {
          case Some(url) =>
            val context = LogManager.getContext(false).asInstanceOf[LoggerContext]
            context.setConfigLocation(url.toURI)
            if (!silent) {
              System.err.println(s"Using Spark's default log4j profile: $defaultLogProps")
            }
          case None =>
            System.err.println(s"Spark was unable to load $defaultLogProps")
        }
      }

      if (Logging.defaultRootLevel == null) {
        Logging.defaultRootLevel = rootLogger.getLevel()
      }

      if (isInterpreter) {
        // Use the repl's main class to define the default log level when running the shell,
        // overriding the root logger's config if they're different.
        val replLogger = LogManager.getLogger(logName).asInstanceOf[Log4jLogger]
        val replLevel = if (Logging.loggerWithCustomConfig(replLogger)) {
          replLogger.getLevel()
        } else {
          Level.WARN
        }
        // Update the consoleAppender threshold to replLevel
        if (replLevel != rootLogger.getLevel()) {
          if (!silent) {
            System.err.printf("Setting default log level to \"%s\".\n", replLevel)
            System.err.println("To adjust logging level use sc.setLogLevel(newLevel). " +
              "For SparkR, use setLogLevel(newLevel).")
          }
        }
      }
      // scalastyle:on println
    }
    Logging.initialized = true

    // Force a call into slf4j to initialize it. Avoids this happening from multiple threads
    // and triggering this: http://mailman.qos.ch/pipermail/slf4j-dev/2010-April/002956.html
    log
  }
}

private object Logging {
  @volatile private var initialized = false
  @volatile private var defaultSparkLog4jConfig = false
  @volatile private var defaultRootLevel: Level = null
  val initLock = new Object()
  try {
    // We use reflection here to handle the case where users remove the
    // slf4j-to-jul bridge order to route their logs to JUL.
    val bridgeClass = Class.forName("org.slf4j.bridge.SLF4JBridgeHandler")
    bridgeClass.getMethod("removeHandlersForRootLogger").invoke(null)
    val installed = bridgeClass.getMethod("isInstalled").invoke(null).asInstanceOf[Boolean]
    if (!installed) {
      bridgeClass.getMethod("install").invoke(null)
    }
  } catch {
    case _: ClassNotFoundException => // can't log anything yet so just fail silently
  }

  private def isLog4j2(): Boolean = {
    // This distinguishes the log4j 1.2 binding, currently
    // org.slf4j.impl.Log4jLoggerFactory, from the log4j 2.0 binding, currently
    // org.apache.logging.slf4j.Log4jLoggerFactory
    val binderClass = StaticLoggerBinder.getSingleton.getLoggerFactoryClassStr
    "org.apache.logging.slf4j.Log4jLoggerFactory".equals(binderClass)
  }

  private def islog4j2DefaultConfigured(): Boolean = {

    val rootLogger = LogManager.getRootLogger.asInstanceOf[Log4jLogger]
    rootLogger.getAppenders.isEmpty ||
      (rootLogger.getAppenders.size() == 1 &&
        rootLogger.getLevel == Level.ERROR &&
        LogManager.getContext.asInstanceOf[LoggerContext]
          .getConfiguration.isInstanceOf[DefaultConfiguration])
  }

  // Return true if the logger has custom configuration. It depends on:
  // 1. If the logger isn't attached with root logger config (i.e., with custom configuration), or
  // 2. the logger level is different to root config level (i.e., it is changed programmatically).
  //
  // Note that if a logger is programmatically changed log level but set to same level
  // as root config level, we cannot tell if it is with custom configuration.
  private def loggerWithCustomConfig(logger: Log4jLogger): Boolean = {
    val rootConfig = LogManager.getRootLogger.asInstanceOf[Log4jLogger].get()
    (logger.get() ne rootConfig) || (logger.getLevel != rootConfig.getLevel())
  }
}
