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
  val pathSeparator = File.pathSeparator
  def apply(rootPath: String): PathFun = new PathFun(rootPath)

  def joinPath(rootPath: String, paths: String*) = {
    val pf = apply(rootPath)
    for (arg <- paths) pf.add(arg)
    pf.toPath
  }
}
