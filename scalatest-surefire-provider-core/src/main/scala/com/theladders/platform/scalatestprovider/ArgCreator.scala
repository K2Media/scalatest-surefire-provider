package com.theladders.platform.scalatestprovider

import collection.JavaConversions._
import org.apache.maven.surefire.util.TestsToRun

/**
 * @author ckreps
 */
object ArgCreator
{
  def apply(paramReader:ParameterReader) = new ArgCreator(paramReader)
}
class ArgCreator(paramReader:ParameterReader)
{
  private val log = paramReader.logger

  def scalaTestSuite:List[String] =
  {
    argListFromTestsToRun("-s", paramReader.allScalaTestsToRun)
  }

  def junit4TestSuite:List[String] =
  {
    argListFromTestsToRun("-j", paramReader.allJUnit4TestsToRun)
  }

  private def argListFromTestsToRun(argName:String,
                                    testsToRun:TestsToRun):List[String] =
  {
    testsToRun.getLocatedClasses.flatMap(argName :: _.getName :: Nil).toList
  }

  def xmlDirectoryReporter:List[String] =
  {
    val directory = paramReader.reportsDirectory
    if(!directory.exists)
    {
      directory.mkdir
    }
    List("-u", directory.getAbsolutePath)
  }

  def stdOutReporter = List("-o")

  def customReporter = List("-r", classOf[ReportingDelegate].getName)

  /**
   * Returns the arg for turning on parallel test execution if
   * specified in configuration.
   */
  def parallelToggle:List[String] =
  {
    if(paramReader.isParallelRequested) List("-c") else Nil
  }
}