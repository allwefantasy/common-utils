package tech.mlsql.common.utils.lang
import scala.language.experimental.macros

/**
 * 16/3/2021 WilliamZhu(allwefantasy@gmail.com)
 */
package object goland {

  def goScope[T](body: T): T = macro GoAsync.goScopeImpl[T]

  @scala.annotation.compileTimeOnly("defer/recover method usage outside go / goScope ")
  def defer(x: =>Unit): Unit = ???

  @scala.annotation.compileTimeOnly("defer/recover method usage outside go / goScope ")
  def recover[T](f: PartialFunction[Throwable, T]): Boolean = ???
}
