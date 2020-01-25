package tech.mlsql.common.utils.lang.sc

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object CCMapMappable {
  implicit def materializeMappable[T]: CCMapMappable[T] =
  macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: Context): c.Expr[CCMapMappable[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    val (toMapParams, fromMapParams) = fields.map { field ⇒
      val name = field.name.toTermName
      val decoded = name.decodedName.toString

      val returnType = tpe.decl(name).typeSignature

      def isOption = {
        returnType.typeSymbol.toString == "class Option"
      }

      val fromMapStr = if (isOption) {
        q"map.get($decoded).asInstanceOf[$returnType]"
      } else {
        q"map($decoded).asInstanceOf[$returnType]"
      }

      val toMapStr = if (isOption) {
        q"$decoded → t.$name.getOrElse(null)"
      } else {
        q"$decoded → t.$name"
      }

      (toMapStr, fromMapStr)
    }.unzip

    c.Expr[CCMapMappable[T]] {
      q"""          
      new tech.mlsql.common.utils.lang.sc.CCMapMappable[$tpe] {
        def toMap(t: $tpe): Map[String, Any] = Map(..$toMapParams)
        def fromMap(map: Map[String, Any]): $tpe = $companion(..$fromMapParams)
      }
    """
    }
  }
}

trait CCMapMappable[T] {
  def toMap(t: T): Map[String, Any]

  def fromMap(map: Map[String, Any]): T
}
