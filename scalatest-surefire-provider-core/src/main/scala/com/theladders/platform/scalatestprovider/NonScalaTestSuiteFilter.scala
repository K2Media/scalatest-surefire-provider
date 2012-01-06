package com.theladders.platform.scalatestprovider

import org.scalatest.{Spec, FunSuite, FlatSpec, FeatureSpec}

/**
 * @author ckreps
 */
trait NonScalaTestSuiteFilter extends NullFilter
{
  private val validSet =
    Set(
      classOf[FeatureSpec],
      classOf[FlatSpec],
      classOf[FunSuite],
      classOf[Spec])

  override def accept(testClass:Class[_]):Boolean =
  {
    validSet.foldLeft(false)(_ || _.isAssignableFrom(testClass)) && super.accept(testClass)
  }
}