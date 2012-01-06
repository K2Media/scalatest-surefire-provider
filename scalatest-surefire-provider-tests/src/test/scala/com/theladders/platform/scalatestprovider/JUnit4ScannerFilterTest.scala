package com.theladders.platform.scalatestprovider

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{GivenWhenThen, FeatureSpec}
import org.junit.Test
import org.junit.Assert.assertTrue

/**
 * @author ckreps
 */

class JUnit4ScannerFilterTest
  extends FeatureSpec
  with ShouldMatchers
  with GivenWhenThen
{
  feature("A ScannerFilter for JUnit4 correctly identifies JUnit tests.")
  {
    scenario("Passing an annotated JUnit4 test type.")
    {
      given("An example of a JUnit4 test.")
      class IAmAJunitTest
      {
        @Test
        def testSomething:Unit = assertTrue(true)
      }

      given("The Class instance of a JUnit4 test.")
      val junit4Class = classOf[IAmAJunitTest]

      when("Submitting the type to the ScannerFilter.")
      val result = JUnit4ScannerFilter.accept(junit4Class)

      then("The type should be identified as a JUnit4 test")
      result should be (true)
    }

    scenario("Passing an abstract JUnit4 annotated test type.")
    {
      given("An abstract example JUnit4 test.")
      abstract class IAmAnAbstractJUnitTest
      {
        @Test
        def testSomething:Unit = assertTrue(true)
      }

      given("The Class instance")
      val abstractJUnit4Class = classOf[IAmAnAbstractJUnitTest]

      when("Submitting the type to the ScannerFilter")
      val result = JUnit4ScannerFilter.accept(abstractJUnit4Class)

      then("The type should NOT be identified as a JUnit4 test")
      result should be (false)

    }
  }
}