package tech.mlsql.test

import org.scalatest.FunSuite
import tech.mlsql.common.utils.base.Templates
import tech.mlsql.common.utils.func.WowFuncParser
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
    assert(Templates.evaluate(" hello {} ", Seq("jack")) == " hello jack ")
    assert(Templates.evaluate(" hello {0} {1} {0}", Seq("jack", "wow")) == " hello jack wow jack")
    println(Templates.evaluate(" hello {0} {1} {2:uuid()}", Seq("jack", "wow")))
    println(Templates.evaluate(" hello {0} {1} {2:mock:}", Seq("jack", "wow")))

    println(Templates.evaluate(" hello {0} {1} {2:mock} as {-1:next(named,uuid())}", Seq("jack", "wow", "named", "table1")))
    println(Templates.evaluate(" hello {0} {1} {2:mock} as {-1:next(named,uuid())}", Seq("jack", "wow")))

    println(Templates.evaluate(" hello ${jack}  table:${table} ", Seq("_", "-jack", "wow", "-table", "table1")))
  }

  test("template evaluate with func") {
    println(Templates.evaluate(" hello {0} {1} {2:mock} as {-1:next(named,uuid())}", Seq("jack", "wow")))
  }

  test("parse func") {
    val funcParser = new WowFuncParser()
    val func = funcParser.parseFunc("next(named,uuid())").get

    funcParser.printFunc(func,1)

  }
}



