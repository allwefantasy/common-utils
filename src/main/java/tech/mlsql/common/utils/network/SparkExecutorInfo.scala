package tech.mlsql.common.utils.network

import java.net.URL

import scala.collection.mutable

/**
  * 2019-09-04 WilliamZhu(allwefantasy@gmail.com)
  */
object SparkExecutorInfo {
  def getInstance = {
    parseArgs(None)
  }

  /**
    * /opt/java8/bin/java
    * -server -Xmx8192m -Xrunjdwp:transport=dt_socket,server=y,address=7086,suspend=n
    * -Djava.io.tmpdir=/data/yarn/nm/usercache/yuy/appcache/application_1567426485975_12575/container_1567426485975_12575_01_000007/tmp
    * -Dspark.port.maxRetries=30
    * -Dspark.driver.port=46281
    * -Dspark.yarn.app.container.log.dir=/data/yarn/container-logs/application_1567426485975_12575/container_1567426485975_12575_01_000007
    * -XX:OnOutOfMemoryError=kill %p org.apache.spark.executor.CoarseGrainedExecutorBackend
    * --driver-url spark://CoarseGrainedScheduler@172.16.9.9:46281
    * --executor-id 6
    * --hostname node20.uc.host.dxy
    * --cores 1
    * --app-id application_1567426485975_12575 
    * --user-class-path file:/data/yarn/nm/usercache/yuy/appcache/application_1567426485975_12575/container_1567426485975_12575_01_000007/__app__.jar
    *
    * @param s
    * @return
    */
  private def parseArgs(s: Option[String]): SparkExecutorInfo = {
    var psDriverUrl: String = null
    var psExecutorId: String = null
    var hostname: String = null
    var cores: Int = 0
    var appId: String = null
    var workerUrl: Option[String] = None
    val userClassPath = new mutable.ListBuffer[URL]()

    //val runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    //var argv = runtimeMxBean.getInputArguments.toList
    var argv = s.getOrElse(System.getProperty("sun.java.command")).split("\\s+").toList
    val tempStr = argv.mkString(" ")
    if (!tempStr.contains("--driver-url") || !tempStr.contains("--executor-id")) {
      return SparkExecutorInfo(psDriverUrl, psExecutorId, hostname, cores, appId, workerUrl, userClassPath)
    }

    var count = 0
    var first = 0
    argv.foreach { f =>
      if (f.startsWith("--") && first == 0) {
        first = count
      }
      count += 1
    }
    argv = argv.drop(first)

    while (!argv.isEmpty) {
      argv match {
        case ("--driver-url") :: value :: tail =>
          psDriverUrl = value
          argv = tail
        case ("--executor-id") :: value :: tail =>
          psExecutorId = value
          argv = tail
        case ("--hostname") :: value :: tail =>
          hostname = value
          argv = tail
        case ("--cores") :: value :: tail =>
          cores = value.toInt
          argv = tail
        case ("--app-id") :: value :: tail =>
          appId = value
          argv = tail
        case ("--worker-url") :: value :: tail =>
          // Worker url is used in spark standalone mode to enforce fate-sharing with worker
          workerUrl = Some(value)
          argv = tail
        case ("--user-class-path") :: value :: tail =>
          userClassPath += new URL(value)
          argv = tail
        case Nil =>
        case tail =>
          System.err.println(s"Unrecognized options: ${tail.mkString(" ")}")
          argv = List()
      }
    }

    SparkExecutorInfo(psDriverUrl, psExecutorId, hostname, cores, appId, workerUrl, userClassPath)
  }

}

case class SparkExecutorInfo(var psDriverUrl: String = null,
                             var psExecutorId: String = null,
                             var hostname: String = null,
                             var cores: Int = 0,
                             var appId: String = null,
                             var workerUrl: Option[String] = None,
                             var userClassPath: mutable.ListBuffer[URL] = new mutable.ListBuffer[URL]())
