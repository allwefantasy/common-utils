package tech.mlsql.common.utils.lang.goland

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.util.control.{ControlThrowable, NonFatal}

class Defers[T]
{

  /**
   * can be used in main block
   * (which can be plain or async)
   * and store body for defered execution after
   * evaluation of main block
   **/
  def defer(body: =>Unit): Unit =
  {
    var prev = rl.get
    var next = (()=>body)::prev
    while(!rl.compareAndSet(prev,next)) {
      prev = rl.get
      next = (()=>body)::prev
    }
  }

  /**
   * called after execution of main block, where
   * all 'defered' blocks will be executed in one thread
   * in LIFO order.
   */
  def processResult(x: Try[T]):T =
  {
    tryProcess(x) match {
      case Success(v) => v
      case Failure(ex) =>
        throw ex
    }
  }

  def tryProcess(x: Try[T]):Try[T] =
  {
    last = x
    unroll(rl getAndSet Nil)
    last
  }

  /**
   * called inside defer blocks, where argument(t)
   */
  def recover(f: PartialFunction[Throwable,T]): Boolean = {
    var retval = false
    for(e <- last.failed if (f.isDefinedAt(e) && !e.isInstanceOf[ControlThrowable])) {
      last = Success(f(e))
      retval=true
    }
    retval
  }

  @tailrec
  private[this] def unroll(l: List[()=>Unit]):Try[T] =
    l match {
      case Nil => last
      case head::tail => try {
        head()
      } catch {
        case ex: Throwable =>
          last=Failure(ex)
      }
        // first component is for defer inside defer
        unroll(rl.getAndSet(Nil) ++ tail)
    }

  private[this] var last: Try[T] = Failure(Defers.NoResultException())

  private[this] val rl: AtomicReference[List[()=>Unit]] = new AtomicReference(List())
}

object Defers
{

  class NoResultException extends RuntimeException

  object NoResultException
  {
    def apply() = new NoResultException()
  }

  /**
   * same as scala.util.Try with one difference:
   *ControlThrowable is catched and mapped to Failure.
   */
  def controlTry[T](body: =>T):Try[T] =
  {
    try {
      Success(body)
    } catch {
      case ex: ControlThrowable => Failure(ex)
      case NonFatal(ex) => Failure(ex)
    }
  }

}

/**
 * syntax sugar, for calling Defers.
 */
object withDefer
{

  def apply[A](f: Defers[A] => A):A =
  { val d = new Defers[A]()
    d.processResult(Defers.controlTry(f(d)))
  }

  def asTry[A](f: Defers[A] => A) =
  { val d = new Defers[A]()
    d.tryProcess(Defers.controlTry(f(d)))
  }

}
