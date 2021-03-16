package tech.mlsql.test

import org.scalatest.FunSuite
import tech.mlsql.common.utils.lang.goland._

/**
 * 16/3/2021 WilliamZhu(allwefantasy@gmail.com)
 */
class GoDeffer extends FunSuite {
  test("defer") {
    def resourceClose = goScope {
      defer {
        println(2)
      }
      println(1)
    }

    resourceClose

    def exceptionHandle = goScope {
      defer {
        recover {
          case e: Throwable => println(e.getMessage)
            "wow"
        }
      }
      throw new Exception("i am exception should be print")
      ""
    }
    exceptionHandle
  }

}
