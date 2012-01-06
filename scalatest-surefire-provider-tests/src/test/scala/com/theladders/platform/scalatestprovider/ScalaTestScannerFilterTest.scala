package com.theladders.platform.testplus

import org.scalatest.matchers.ShouldMatchers
import org.scalatest._
import com.theladders.platform.scalatestprovider.ScalaTestScannerFilter

/**
 * @author ckreps
 */
class ScalaTestScannerFilterTest
  extends FeatureSpec
  with ShouldMatchers
  with GivenWhenThen
{
  feature("A ScannerFilter for ScalaTest correctly identifies ScalaTest tests.")
  {
    scenario("Passing an instance of each type of ScalaTest test to the filter.")
    {
      given("Examples of each concrete type ScalaTest can execute.")
      class FeatureSpecExample extends FeatureSpec
      class FlatSpecExample extends FlatSpec
      class FunSuiteExample extends FunSuite
      class SpecExample extends Spec

      given("The complete set of class types the ScalaTest runner can run.")
      val validSet =
        Set(
          classOf[FeatureSpecExample],
          classOf[FlatSpecExample],
          classOf[FunSuiteExample],
          classOf[SpecExample])


      when("Each class type is submitted to the ScannerFilter.")
      then("All returned values should be true.")
      validSet.foreach(c => {
        ScalaTestScannerFilter.accept(c) should be (true)
      })
    }

    scenario("Passing various non-ScalaTest instances to the filter to try and trick it.")
    {
      given("Example types ScalaTest should not try to execute.")
      class EndsInTheWordTest extends Object

      given("A set of class types the ScalaTest runner cannot execute.")
      val invalidSet =
        Set(
          classOf[EndsInTheWordTest],
          classOf[Option[String]],
          classOf[Object],
          classOf[Nothing]
        )

      when("Each class type is submitted to the ScannerFilter.")
      then("All returned values should be false.")
      invalidSet.foreach(c => {
        ScalaTestScannerFilter.accept(c) should be (false)
      })
    }
  }



}