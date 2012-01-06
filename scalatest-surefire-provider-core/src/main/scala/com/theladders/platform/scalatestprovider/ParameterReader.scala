package com.theladders.platform.scalatestprovider

import org.apache.maven.surefire.providerapi.ProviderParameters
import org.apache.maven.surefire.report.ReporterFactory
import java.io.File
import org.apache.maven.surefire.util.{ScannerFilter, TestsToRun}
import collection.JavaConversions._

/**
 * @author ckreps
 */
object ParameterReader
{
  def apply(params:ProviderParameters) =
    new ParameterReader(params)
}

class ParameterReader(params:ProviderParameters)
{
  private val scanner = params.getDirectoryScanner

  def logger = params.getConsoleLogger

  private val PARALLEL_PROP_KEY = "parallel"
  private val PARALLEL_ON_VALUE = "true"

  def allScalaTestsToRun:TestsToRun =
  {
    findTestClasses(ScalaTestScannerFilter)
  }

  def allJUnit4TestsToRun:TestsToRun =
  {
    findTestClasses(JUnit4ScannerFilter)
  }

  /**
   * Checks if parallel execution is desired.
   *
   * Specified like this in the pom:
   * <parallel>true</parallel>
   *
   * The default JUnit and TestNG surefire providers
   * have the same configuration option but require
   * the value to be either "classes" or "method" indicating
   * tests should be run in parallel at the class or method
   * level.  ScalaTest blurs the line between a class and method
   * by supporting arbitrary nesting of it's tests which are
   * run in parallel depth first.  You might say this is "method"
   * level but it's not really since you can't nest test methods
   * in JUnit or TestNG.  This is the only way ScalaTest does parallel
   * execution so we just check for a value of "true".
   */
  def isParallelRequested:Boolean =
  {
    val value = providerProperty(PARALLEL_PROP_KEY)
    value != null && value.equalsIgnoreCase(PARALLEL_ON_VALUE)
  }

  private def findTestClasses(filter:ScannerFilter):TestsToRun =
  {
    val scanResult = scanner.locateTestClasses(
      params.getTestClassLoader,
      filter)

    params.getRunOrderCalculator.orderTestClasses(scanResult)
  }

  def reporterFactory:ReporterFactory = params.getReporterFactory

  def reportsDirectory:File =
  {
    params
      .getReporterConfiguration
      .getReportsDirectory
  }

  private def providerProperty(key:String):String =
  {
    params.getProviderProperties.get(key).asInstanceOf[String]
  }
}