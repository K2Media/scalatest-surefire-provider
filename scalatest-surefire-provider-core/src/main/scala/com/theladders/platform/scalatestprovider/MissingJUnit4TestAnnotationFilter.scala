package com.theladders.platform.scalatestprovider

import org.junit.Test

/**
 * @author ckreps
 */
trait MissingJUnit4TestAnnotationFilter extends NullFilter
{
  override def accept(testClass:Class[_]):Boolean =
  {
    testClass.getDeclaredMethods
      .flatMap(_.getAnnotations)
      .map(_.annotationType)
      .contains(classOf[Test]) && super.accept(testClass)
  }
}