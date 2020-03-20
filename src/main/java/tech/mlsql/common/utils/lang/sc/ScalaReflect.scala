package tech.mlsql.common.utils.lang.sc


import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._

/**
 * 2019-05-01 WilliamZhu(allwefantasy@gmail.com)
 */

class ScalaReflect {}

object ScalaReflect {
  private def mirror = {
    ru.runtimeMirror(getClass.getClassLoader)
  }

  def fromInstance[T: ClassTag](obj: T) = {
    val x = mirror.reflect[T](obj)
    new ScalaMethodReflect(x)
  }

  def fromObject[T: ru.TypeTag]() = {
    new ScalaModuleReflect(ru.typeOf[T].typeSymbol.companion.asModule)
  }

  def fromObjectStr(str: String) = {
    val module = mirror.staticModule(str)
    new ScalaModuleReflect(module)
  }

  def ccFromMap[T: TypeTag : ClassTag](m: Map[String, _]) = {
    import scala.reflect._
    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)

    val constructorArgs = constructor.paramLists.flatten.map((param: Symbol) => {
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        m.get(paramName)
      else
        m.get(paramName).getOrElse(throw new IllegalArgumentException("Map is missing required parameter named " + paramName))
    })

    constructorMirror(constructorArgs: _*).asInstanceOf[T]
  }

  def ccToMap[T: TypeTag : ClassTag](t: T): Map[String, Any] = {
    import scala.reflect._
    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod

    val tempMap = new mutable.HashMap[String, Any]()
    constructor.paramLists.flatten.foreach((param: Symbol) => {
      val paramName = param.name.toString
      if (param.typeSignature <:< typeOf[Option[Any]])
        tempMap.put(paramName, rm.reflect(t).reflectField(param.asTerm).get)
      else
        tempMap.put(paramName, rm.reflect(t).reflectField(param.asTerm).get)
    })
    tempMap.toMap
  }


}

class ScalaModuleReflect(x: ru.ModuleSymbol) {

  private var methodName: Option[ru.MethodSymbol] = None
  private var fieldName: Option[ru.TermSymbol] = None

  def method(name: String) = {
    methodName = Option(x.typeSignature.member(ru.TermName(name)).asMethod)
    this
  }

  def field(name: String) = {
    fieldName = Option(x.typeSignature.member(ru.TermName(name)).asTerm)
    this
  }

  private def mirror = {
    ru.runtimeMirror(getClass.getClassLoader)
  }

  def invoke(objs: Any*) = {

    if (methodName.isDefined) {
      val instance = mirror.reflectModule(x).instance
      mirror.reflect(instance).reflectMethod(methodName.get.asMethod)(objs)
    } else if (fieldName.isDefined) {
      val instance = mirror.reflectModule(x).instance
      val fieldMirror = mirror.reflect(instance).reflectField(fieldName.get)
      if (objs.size > 0) {
        fieldMirror.set(objs.toSeq(0))
      }
      fieldMirror.get

    } else {
      throw new IllegalArgumentException("Can not invoke `invoke` without call method or field function")
    }

  }
}

class ScalaMethodReflect(x: ru.InstanceMirror) {

  private var methodName: Option[ru.MethodSymbol] = None
  private var fieldName: Option[ru.TermSymbol] = None

  def method(name: String) = {
    methodName = Option(x.symbol.typeSignature.member(ru.TermName(name)).asMethod)
    this
  }

  def field(name: String) = {
    fieldName = Option(x.symbol.typeSignature.member(ru.TermName(name)).asTerm)
    this
  }

  def invoke(objs: Any*) = {

    if (methodName.isDefined) {
      x.reflectMethod(methodName.get.asMethod)(objs: _*)
    } else if (fieldName.isDefined) {
      val fieldMirror = x.reflectField(fieldName.get)
      if (objs.size > 0) {
        fieldMirror.set(objs.toSeq(0))
      }
      fieldMirror.get

    } else {
      throw new IllegalArgumentException("Can not invoke `invoke` without call method or field function")
    }


  }
}
