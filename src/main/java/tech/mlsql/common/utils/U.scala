package tech.mlsql.common.utils

import java.io.File
import java.lang.management.{LockInfo, ManagementFactory, MonitorInfo, ThreadInfo}
import java.net.URI

import tech.mlsql.common.utils.base.CharMatcher
import tech.mlsql.common.utils.io.IOTool
import tech.mlsql.common.utils.log.Logging
import tech.mlsql.common.utils.net.NetTool
import tech.mlsql.common.utils.os.SystemUtils

import scala.collection.JavaConverters._
import scala.xml.{NodeSeq, Text}

/**
 * 19/3/2020 WilliamZhu(allwefantasy@gmail.com)
 */
object U extends Logging {
  // process related
  /**
   * Returns the name of this JVM process. This is OS dependent but typically (OSX, Linux, Windows),
   * this is formatted as PID@hostname.
   */
  def getProcessName(): String = {
    SystemUtils.getProcessName()
  }

  // IO related

  /**
   * Returns a path of temporary file which is in the same directory with `path`.
   */
  def tempFileWith(path: File): File = {
    IOTool.tempFileWith(path)
  }

  // ip/hostname related
  /**
   * Get the local machine's hostname.
   */
  def localHost = {
    NetTool.localHostName
  }


  /**
   * Returns the system properties map that is thread-safe to iterator over. It gets the
   * properties which have been set explicitly, as well as those for which only a default value
   * has been defined.
   */
  def getSystemProperties: Map[String, String] = {
    System.getProperties.stringPropertyNames().asScala
      .map(key => (key, System.getProperty(key))).toMap
  }


  /**
   * Method executed for repeating a task for side effects.
   * Unlike a for comprehension, it permits JVM JIT optimization
   */
  def times(numIters: Int)(f: => Unit): Unit = {
    var i = 0
    while (i < numIters) {
      f
      i += 1
    }
  }

  /**
   * Timing method based on iterations that permit JVM JIT optimization.
   *
   * @param numIters number of iterations
   * @param f        function to be executed. If prepare is not None, the running time of each call to f
   *                 must be an order of magnitude longer than one millisecond for accurate timing.
   * @param prepare  function to be executed before each call to f. Its running time doesn't count.
   * @return the total time across all iterations (not counting preparation time)
   */
  def timeIt(numIters: Int)(f: => Unit, prepare: Option[() => Unit] = None): Long = {
    if (prepare.isEmpty) {
      val start = System.currentTimeMillis
      times(numIters)(f)
      System.currentTimeMillis - start
    } else {
      var i = 0
      var sum = 0L
      while (i < numIters) {
        prepare.get.apply()
        val start = System.currentTimeMillis
        f
        sum += System.currentTimeMillis - start
        i += 1
      }
      sum
    }
  }

  /**
   * Counts the number of elements of an iterator using a while loop rather than calling
   * [[scala.collection.Iterator#size]] because it uses a for loop, which is slightly slower
   * in the current version of Scala.
   */
  def getIteratorSize(iterator: Iterator[_]): Long = {
    var count = 0L
    while (iterator.hasNext) {
      count += 1L
      iterator.next()
    }
    count
  }

  /**
   * Generate a zipWithIndex iterator, avoid index value overflowing problem
   * in scala's zipWithIndex
   */
  def getIteratorZipWithIndex[T](iterator: Iterator[T], startIndex: Long): Iterator[(T, Long)] = {
    new Iterator[(T, Long)] {
      require(startIndex >= 0, "startIndex should be >= 0.")
      var index: Long = startIndex - 1L

      def hasNext: Boolean = iterator.hasNext

      def next(): (T, Long) = {
        index += 1L
        (iterator.next(), index)
      }
    }
  }

  /** Return the class name of the given object, removing all dollar signs */
  def getFormattedClassName(obj: AnyRef): String = {
    getSimpleName(obj.getClass).replace("$", "")
  }

  /**
   * Safer than Class obj's getSimpleName which may throw Malformed class name error in scala.
   * This method mimicks scalatest's getSimpleNameOfAnObjectsClass.
   */
  def getSimpleName(cls: Class[_]): String = {
    try {
      return cls.getSimpleName
    } catch {
      case err: InternalError => return stripDollars(stripPackages(cls.getName))
    }
  }

  /**
   * Remove the packages from full qualified class name
   */
  private def stripPackages(fullyQualifiedName: String): String = {
    fullyQualifiedName.split("\\.").takeRight(1)(0)
  }


  /**
   * Check the validity of the given Kubernetes master URL and return the resolved URL. Prefix
   * "k8s://" is appended to the resolved URL as the prefix is used by KubernetesClusterManager
   * in canCreate to determine if the KubernetesClusterManager should be used.
   */
  def checkAndGetK8sMasterUrl(rawMasterURL: String): String = {
    require(rawMasterURL.startsWith("k8s://"),
      "Kubernetes master URL must start with k8s://.")
    val masterWithoutK8sPrefix = rawMasterURL.substring("k8s://".length)

    // To handle master URLs, e.g., k8s://host:port.
    if (!masterWithoutK8sPrefix.contains("://")) {
      val resolvedURL = s"https://$masterWithoutK8sPrefix"
      logInfo("No scheme specified for kubernetes master URL, so defaulting to https. Resolved " +
        s"URL is $resolvedURL.")
      return s"k8s://$resolvedURL"
    }

    val masterScheme = new URI(masterWithoutK8sPrefix).getScheme
    val resolvedURL = masterScheme.toLowerCase match {
      case "https" =>
        masterWithoutK8sPrefix
      case "http" =>
        logWarning("Kubernetes master URL uses HTTP instead of HTTPS.")
        masterWithoutK8sPrefix
      case null =>
        val resolvedURL = s"https://$masterWithoutK8sPrefix"
        logInfo("No scheme specified for kubernetes master URL, so defaulting to https. Resolved " +
          s"URL is $resolvedURL.")
        resolvedURL
      case _ =>
        throw new IllegalArgumentException("Invalid Kubernetes master scheme: " + masterScheme)
    }

    s"k8s://$resolvedURL"
  }


  /**
   * Regular expression matching full width characters.
   *
   * Looked at all the 0x0000-0xFFFF characters (unicode) and showed them under Xshell.
   * Found all the full width characters, then get the regular expression.
   */
  private val fullWidthRegex = ("""[""" +
    // scalastyle:off nonascii
    """\u1100-\u115F""" +
    """\u2E80-\uA4CF""" +
    """\uAC00-\uD7A3""" +
    """\uF900-\uFAFF""" +
    """\uFE10-\uFE19""" +
    """\uFE30-\uFE6F""" +
    """\uFF00-\uFF60""" +
    """\uFFE0-\uFFE6""" +
    // scalastyle:on nonascii
    """]""").r

  /**
   * Return the number of half widths in a given string. Note that a full width character
   * occupies two half widths.
   *
   * For a string consisting of 1 million characters, the execution of this method requires
   * about 50ms.
   */
  def stringHalfWidth(str: String): Int = {
    if (str == null) 0 else str.length + fullWidthRegex.findAllIn(str).size
  }

  /**
   * Remove trailing dollar signs from qualified class name,
   * and return the trailing part after the last dollar sign in the middle
   */
  private def stripDollars(s: String): String = {
    val lastDollarIndex = s.lastIndexOf('$')
    if (lastDollarIndex < s.length - 1) {
      // The last char is not a dollar sign
      if (lastDollarIndex == -1 || !s.contains("$iw")) {
        // The name does not have dollar sign or is not an intepreter
        // generated class, so we should return the full string
        s
      } else {
        // The class name is intepreter generated,
        // return the part after the last dollar sign
        // This is the same behavior as getClass.getSimpleName
        s.substring(lastDollarIndex + 1)
      }
    }
    else {
      // The last char is a dollar sign
      // Find last non-dollar char
      val lastNonDollarChar = s.reverse.find(_ != '$')
      lastNonDollarChar match {
        case None => s
        case Some(c) =>
          val lastNonDollarIndex = s.lastIndexOf(c)
          if (lastNonDollarIndex == -1) {
            s
          } else {
            // Strip the trailing dollar signs
            // Invoke stripDollars again to get the simple name
            stripDollars(s.substring(0, lastNonDollarIndex + 1))
          }
      }
    }
  }

  /**
   * Whether the underlying operating system is Windows.
   */
  val isWindows = SystemUtils.IS_OS_WINDOWS

  /**
   * Whether the underlying operating system is Mac OS X.
   */
  val isMac = SystemUtils.IS_OS_MAC_OSX

  /**
   * Pattern for matching a Windows drive, which contains only a single alphabet character.
   */
  val windowsDrive = "([a-zA-Z])".r


  // number related

  //string related
  //private def isSpace(c: Char): Boolean = {
  //    " \t\r\n".indexOf(c) != -1
  //  }
  def isSpace(char: Char) = {
    CharMatcher.WHITESPACE.matches(char)
  }

  private implicit class Lock(lock: LockInfo) {
    def lockString: String = {
      lock match {
        case monitor: MonitorInfo =>
          s"Monitor(${lock.getClassName}@${lock.getIdentityHashCode}})"
        case _ =>
          s"Lock(${lock.getClassName}@${lock.getIdentityHashCode}})"
      }
    }
  }

  def getThreadDumpForThread(threadId: Long): Option[ThreadStackTrace] = {
    if (threadId <= 0) {
      None
    } else {
      // The Int.MaxValue here requests the entire untruncated stack trace of the thread:
      val threadInfo =
        Option(ManagementFactory.getThreadMXBean.getThreadInfo(threadId, Int.MaxValue))
      threadInfo.map(threadInfoToThreadStackTrace)
    }
  }

  private def threadInfoToThreadStackTrace(threadInfo: ThreadInfo): ThreadStackTrace = {
    val monitors = threadInfo.getLockedMonitors.map(m => m.getLockedStackFrame -> m).toMap
    val stackTrace = StackTrace(threadInfo.getStackTrace.map { frame =>
      monitors.get(frame) match {
        case Some(monitor) =>
          monitor.getLockedStackFrame.toString + s" => holding ${monitor.lockString}"
        case None =>
          frame.toString
      }
    })

    // use a set to dedup re-entrant locks that are held at multiple places
    val heldLocks =
      (threadInfo.getLockedSynchronizers ++ threadInfo.getLockedMonitors).map(_.lockString).toSet

    ThreadStackTrace(
      threadId = threadInfo.getThreadId,
      threadName = threadInfo.getThreadName,
      threadState = threadInfo.getThreadState,
      stackTrace = stackTrace,
      blockedByThreadId =
        if (threadInfo.getLockOwnerId < 0) None else Some(threadInfo.getLockOwnerId),
      blockedByLock = Option(threadInfo.getLockInfo).map(_.lockString).getOrElse(""),
      holdingLocks = heldLocks.toSeq)
  }

  /**
   * configure a new log4j level
   */
  def setLogLevel(l: org.apache.log4j.Level) {
    org.apache.log4j.Logger.getRootLogger().setLevel(l)
  }

}

case class StackTrace(elems: Seq[String]) {
  override def toString: String = elems.mkString

  def html: NodeSeq = {
    val withNewLine = elems.foldLeft(NodeSeq.Empty) { (acc, elem) =>
      if (acc.isEmpty) {
        acc :+ Text(elem)
      } else {
        acc :+ <br/> :+ Text(elem)
      }
    }

    withNewLine
  }

  def mkString(start: String, sep: String, end: String): String = {
    elems.mkString(start, sep, end)
  }
}

case class ThreadStackTrace(
                             val threadId: Long,
                             val threadName: String,
                             val threadState: Thread.State,
                             val stackTrace: StackTrace,
                             val blockedByThreadId: Option[Long],
                             val blockedByLock: String,
                             val holdingLocks: Seq[String])
