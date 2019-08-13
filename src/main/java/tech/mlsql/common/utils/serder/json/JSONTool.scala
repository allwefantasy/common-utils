package tech.mlsql.common.utils.serder.json

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import net.liftweb.{json => SJSon}

/**
  * Created by allwefantasy on 2/8/2018.
  */
object JSONTool {
  def parseJson[T](str: String)(implicit m: Manifest[T]) = {
    implicit val formats = SJSon.DefaultFormats
    SJSon.parse(str).extract[T]
  }

  def toJsonStr(item: AnyRef) = {
    implicit val formats = SJSon.Serialization.formats(SJSon.NoTypeHints)
    SJSon.Serialization.write(item)
  }

  def pretty(item: AnyRef) = {
    implicit val formats = SJSon.Serialization.formats(SJSon.NoTypeHints)
    SJSon.Serialization.writePretty(item)
  }
}

object JsonUtils {
  /** Used to convert between classes and JSON. */
  val mapper = new ObjectMapper with ScalaObjectMapper
  mapper.setSerializationInclusion(Include.NON_NULL)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.registerModule(DefaultScalaModule)

  def toJson[T: Manifest](obj: T): String = {
    mapper.writeValueAsString(obj)
  }

  def toPrettyJson[T: Manifest](obj: T): String = {
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
  }

  def fromJson[T: Manifest](json: String): T = {
    mapper.readValue[T](json)
  }
}

