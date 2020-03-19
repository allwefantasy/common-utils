package tech.mlsql.test

import org.scalatest.FunSuite
import tech.mlsql.common.utils.base.Templates
import tech.mlsql.common.utils.names.NameConvert

case class TestCase(name: Option[String], id: Int)

class ScalaReflectSpec extends FunSuite {
  test("ccFromMap") {
    //    val maps = Map("id" -> 1)
    //    val tc = ScalaReflect.ccFromMap[TestCase](maps)
    //    assert(tc.id == 1)
    //    assert(tc.name == None)
  }

  test("ow") {
    assert(NameConvert.camelToUnderScore("JackCool") == "jack_cool")
  }

  test("template evaluate") {
    assert(Templates.evaluate(" hello {} ",Seq("jack"))==" hello jack ")
    assert(Templates.evaluate(" hello {0} {1} {0}",Seq("jack","wow"))==" hello jack wow jack")
  }
}



