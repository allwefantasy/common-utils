package tech.mlsql.common.utils.lang.goland

import scala.language.experimental.macros
import scala.language.reflectiveCalls
import scala.reflect.macros.blackbox.Context

object GoAsync{
  def goScopeImpl[T:c.WeakTypeTag](c:Context)(body:c.Expr[T]):c.Expr[T] =
  {
    import c.universe._
    if (containsDefer(c)(body)) {
      val nbody = transformDefer[T](c)(body.tree)
      c.Expr[T](q"""{implicit val defered = new tech.mlsql.common.utils.lang.goland.Defers[${c.weakTypeOf[T]}]()
                      defered.processResult(tech.mlsql.common.utils.lang.goland.Defers.controlTry(${c.untypecheck(nbody)}))
                     }""")
    } else {
      body
    }
  }

  def containsDefer[T:c.WeakTypeTag](c:Context)(body:c.Expr[T]):Boolean =
  {
    import c.universe._
    val findDefer = new Traverser {
      var found = false
      override def traverse(tree:Tree):Unit =
      {
        if (!found) {
          tree match {
            case q"tech.mlsql.common.utils.lang.goland.`package`.defer(..${args})" => found = true
            case _ => super.traverse(tree)
          }
        }
      }
    }
    findDefer traverse body.tree
    findDefer.found
  }

  def transformDefer[T:c.WeakTypeTag](c:Context)(body:c.Tree):c.Tree =
  {
    import c.universe._
    val transformer = new Transformer {
      override def transform(tree:Tree):Tree =
        tree match {
          case q"tech.mlsql.common.utils.lang.goland.`package`.defer(..${args})" =>
            val transformedArgs = args.map(x => Block(transform(x)))
            q"implicitly[tech.mlsql.common.utils.lang.goland.Defers[${weakTypeOf[T]}]].defer(..${transformedArgs} )"
          case q"tech.mlsql.common.utils.lang.goland.`package`.recover[$tps](..${args})" =>
            val transformedArgs = args.map(x => transform(x))
            q"implicitly[tech.mlsql.common.utils.lang.goland.Defers[${weakTypeOf[T]}]].recover(..${transformedArgs} )"
          case _ =>
            super.transform(tree)
        }
    }
    transformer.transform(body)
  }
  
}
