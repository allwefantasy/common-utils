package tech.mlsql.common.utils.lang.sc

object ScalaObjectReflect {
  def findObjectMethod(clzzName: String) = {
    val clzz = Class.forName(clzzName + "$")
    val instance = clzz.getField("MODULE$").get(null)
    (instance.getClass, instance)
  }
}
