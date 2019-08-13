package tech.mlsql.common.utils.lang.sc

import scala.reflect.runtime.universe._

/**
  * 2019-03-11 WilliamZhu(allwefantasy@gmail.com)
  */
object ScalaEnumTool {

  def valueSymbols[E <: Enumeration : TypeTag] = {

    val valueType = typeOf[E#Value]
    typeOf[E].members.filter(sym => !sym.isMethod &&
      sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType && ! sym.isType
    )
  }
}
