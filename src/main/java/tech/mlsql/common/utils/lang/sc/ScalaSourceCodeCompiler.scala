/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.mlsql.common.utils.lang.sc

import tech.mlsql.common.utils.cache.{CacheBuilder, CacheLoader}
import javax.tools._
import tech.mlsql.common.utils.lang.ja.{JavaByteObject, JavaReflect}
import tech.mlsql.common.utils.lang.py.PythonInterp
import tech.mlsql.common.utils.log.Logging

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._


/**
  * Created by allwefantasy on 27/8/2018.
  */
object SourceCodeCompiler extends Logging {
  private val scriptCache = CacheBuilder.newBuilder()
    .maximumSize(10000)
    .build(
      new CacheLoader[ScriptCacheKey, AnyRef]() {
        override def load(scriptCacheKey: ScriptCacheKey): AnyRef = {
          val startTime = System.nanoTime()
          val res = scriptCacheKey.lan match {
            case "python" => PythonInterp.compilePython(scriptCacheKey.code, scriptCacheKey.className)
            case _ => compileScala(prepareScala(scriptCacheKey.code, scriptCacheKey.className))
          }

          def timeMs: Double = (System.nanoTime() - startTime).toDouble / 1000000

          logInfo(s"generate udf time:${timeMs}")
          res
        }
      })

  def execute(scriptCacheKey: ScriptCacheKey): AnyRef = {
    scriptCache.get(scriptCacheKey)
  }

  def newInstance(clazz: Class[_]): Any = {
    val constructor = clazz.getDeclaredConstructors.head
    constructor.setAccessible(true)
    constructor.newInstance()
  }

  def getMethod(clazz: Class[_], method: String) = {
    val candidate = clazz.getDeclaredMethods.filter(_.getName == method).filterNot(_.isBridge)
    if (candidate.isEmpty) {
      throw new Exception(s"No method $method found in class ${clazz.getCanonicalName}")
    } else if (candidate.length > 1) {
      throw new Exception(s"Multiple method $method found in class ${clazz.getCanonicalName}")
    } else {
      candidate.head
    }
  }

  def getFunReturnType(fun: String): Type = {
    import scala.tools.reflect.ToolBox
    var classLoader = Thread.currentThread().getContextClassLoader
    if (classLoader == null) {
      classLoader = scala.reflect.runtime.universe.getClass.getClassLoader
    }
    val tb = runtimeMirror(classLoader).mkToolBox()
    val tree = tb.parse(fun)
    val defDef = tb.typecheck(tree).asInstanceOf[DefDef]
    defDef.tpt.tpe
  }

  def compileScala(src: String): Class[_] = {
    import scala.reflect.runtime.universe
    import scala.tools.reflect.ToolBox
    //val classLoader = scala.reflect.runtime.universe.getClass.getClassLoader
    var classLoader = Thread.currentThread().getContextClassLoader
    if (classLoader == null) {
      classLoader = scala.reflect.runtime.universe.getClass.getClassLoader
    }
    val tb = universe.runtimeMirror(classLoader).mkToolBox()
    val tree = tb.parse(src)
    val clazz = tb.compile(tree).apply().asInstanceOf[Class[_]]
    clazz
  }

  def prepareScala(src: String, className: String): String = {
    src + "\n" + s"scala.reflect.classTag[$className].runtimeClass"
  }

  def compileJava(src: String, className: String): Class[_] = {
    val compiler = ToolProvider.getSystemJavaCompiler
    val diagnostics: DiagnosticCollector[JavaFileObject] = new DiagnosticCollector[JavaFileObject]
    val byteObject: JavaByteObject = new JavaByteObject(className)
    val standardFileManager: StandardJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null)
    val fileManager: JavaFileManager = JavaReflect.createFileManager(standardFileManager, byteObject)
    val task = compiler.getTask(null, fileManager, diagnostics, null, null, JavaReflect.getCompilationUnits(className, src))
    if (!task.call()) {
      diagnostics.getDiagnostics.foreach(println)
    }
    fileManager.close()
    val inMemoryClassLoader = JavaReflect.createClassLoader(byteObject)
    val clazz = inMemoryClassLoader.loadClass(className)
    clazz
  }
}

class SourceCodeCompiler

case class ScriptCacheKey(code: String, className: String, lan: String = "scala")

