package tech.mlsql.common.utils.path

import java.io.File

import scala.collection.mutable.ArrayBuffer

/**
 * 2019-04-25 WilliamZhu(allwefantasy@gmail.com)
 */
class PathFun(rootPath: String) {

  private val buffer = new ArrayBuffer[String]()
  buffer += rootPath.stripSuffix(PathFun.pathSeparator)

  def add(path: String) = {
    val cleanPath = path.stripPrefix(PathFun.pathSeparator).stripSuffix(PathFun.pathSeparator)
    if (!cleanPath.isEmpty) {
      buffer += cleanPath
    }
    this
  }

  def /(path: String) = {
    add(path)
  }

  def toPath = {
    buffer.mkString(PathFun.pathSeparator)
  }

}

object PathFun {
  private val _tmp = System.getProperty("java.io.tmpdir")
  private val _userDir = System.getProperty("user.dir")
  private val _userHome = System.getProperty("user.home")
  val pathSeparator = File.separator

  def apply(rootPath: String): PathFun = new PathFun(rootPath)

  def joinPath(rootPath: String, paths: String*) = {
    val pf = apply(rootPath)
    for (arg <- paths) pf.add(arg)
    pf.toPath
  }

  def tmp: PathFun = {
    new PathFun(_tmp)
  }

  def current: PathFun = {
    new PathFun(_userDir)
  }

  def home: PathFun = {
    new PathFun(_userHome)
  }

  def join(paths: String*): String = {
    val buffer = ArrayBuffer[String]()
    for (arg <- paths) {
      val cleanPath = arg.stripPrefix(PathFun.pathSeparator).stripSuffix(PathFun.pathSeparator)
      buffer += cleanPath
    }
    buffer.mkString(PathFun.pathSeparator)
  }

}
