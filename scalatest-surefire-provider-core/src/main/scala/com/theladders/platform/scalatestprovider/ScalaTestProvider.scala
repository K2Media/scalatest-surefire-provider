package com.theladders.platform.scalatestprovider

import java.util.{Iterator => JIterator}
import org.apache.maven.surefire.suite.RunResult
import org.apache.maven.surefire.providerapi.{ProviderParameters, SurefireProvider}
import collection.JavaConversions._

/**
 * A SurefireProvider implementation for using the ScalaTest
 * framework as the test runner.
 *
 * It supports mixed Java and Scala source trees.
 *
 * It will run any ScalaTest or JUnit4 test found by the
 * test class DirectoryScanner (which will include everything
 * in your maven test source).
 *
 * Configuration is similar to the default SurefireProvider and for
 * many things is exactly the same.  For example test inclusion/exclusion
 * is configured the same way as the default SurefireProvider.
 *
 * For the most part if your pom has a fairly standard setup you
 * can just drop in this SurefireProvider and it will work.
 *
 * @author ckreps
 */
class ScalaTestProvider(params:ProviderParameters)
  extends SurefireProvider
{
  private val paramReader = ParameterReader(params)
  private val argReader = ArgCreator(paramReader)
  private val log = paramReader.logger

  /**
   * This is only called when forkMode isn't the default.
   * The SurefireProvider interface (as of version 2.10) is a little fuzzy
   * on exactly what implementations are supposed to do when this method is invoked.
   * It's not called if forkMode is left at its default setting so
   * we'll fail fast here.
   */
  // TODO Support non-default forkMode
  def getSuites():JIterator[_] =
  {
    throw new UnsupportedOperationException("Support for non-default forkMode not implemented.")
  }


  def invoke(forkTestSet:Object):RunResult =
  {
    invokeRunMethodWithArgs(findScalaTestRunnerArgs)

    ReportBridge.flushTo(paramReader.reporterFactory.createReporter)

    paramReader.reporterFactory.close()
  }

  def findScalaTestRunnerArgs:List[String] =
  {
    argReader.stdOutReporter :::
      argReader.customReporter :::
      argReader.scalaTestSuite :::
      argReader.junit4TestSuite :::
      argReader.parallelToggle
  }

  def invokeRunMethodWithArgs(args:List[String])
  {
    val classLoader = params.getTestClassLoader
    val runMethod = classLoader
      .loadClass("org.scalatest.tools.Runner")
      .getMethod("run", classOf[Array[String]])

    runMethod.invoke(null, args.toArray)
  }

  def cancel()
  {

  }

}