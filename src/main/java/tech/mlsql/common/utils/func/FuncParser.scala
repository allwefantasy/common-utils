package tech.mlsql.common.utils.func

import scala.collection.mutable.ArrayBuffer


class WowFuncParser {

  def printFunc(s: WowFunc, level: Int = 0):Unit = {
    println("*" * level + s.name)
    s.params.foreach {
      case func: WowFunc => printFunc(func, level + 1)
      case symbo: WowSymbol => println("*" * level + symbo)
    }
  }

  def evaluate(s: WowFunc, executeFunc: (WowExecuteFunc) => WowSymbol): WowSymbol = {
    val tempParams = s.params.map {
      case func@WowFunc(name, params, raw) =>
        if (params.filter(_.isInstanceOf[WowFunc]).length == 0) {
          executeFunc(WowExecuteFunc(name, params.map(_.asInstanceOf[WowSymbol])))
        } else {
          evaluate(func, executeFunc)
        }
      case symbol: WowSymbol => symbol

    }
    executeFunc(WowExecuteFunc(s.name, tempParams))

  }

  def parseFunc(s: String): Option[WowFunc] = {
    if (s.isEmpty) return None
    val nameBuf = ArrayBuffer[Char]()
    val paramBuf = ArrayBuffer[Char]()
    val params = ArrayBuffer[WowExpr]()

    var namePhase = true
    var paramPhase = false
    val charArray = s.toCharArray

    var bracketStart = false
    var bracketEnd = false

    var bracketStartNum = 0
    var bracketEndNum = 0
    try {
      (0 until charArray.length).foreach { index =>
        if (charArray(index) == ',' || index == charArray.length - 1) {
          val temp = String.valueOf(paramBuf.toArray)
          val funcOpt = parseFunc(temp)
          if (funcOpt.isDefined) {
            params += funcOpt.get
          } else {
            params += WowSymbol(temp)
          }
          paramBuf.clear()
        } else if (paramPhase) {
          paramBuf += charArray(index)
        }

        if (charArray(index) == '(') {
          namePhase = false
          paramPhase = true
          bracketStart = true
        }


        if (charArray(index) == ')') {
          bracketEnd = true
        }

        if (namePhase) {
          nameBuf += charArray(index)
        }
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }

    if (!bracketStart || !bracketEnd) return None

    Option(WowFunc(String.valueOf(nameBuf.toArray).trim(), params.toArray, s))
  }
}

case class WowExecuteFunc(name: String, params: Array[WowSymbol])

sealed abstract class WowExpr(raw: String)

case class WowFunc(name: String, params: Array[WowExpr], raw: String) extends WowExpr(raw)

case class WowSymbol(name: String) extends WowExpr(name)
