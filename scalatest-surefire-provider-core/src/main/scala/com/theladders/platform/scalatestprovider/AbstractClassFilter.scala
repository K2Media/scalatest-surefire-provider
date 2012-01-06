package com.theladders.platform.scalatestprovider

import java.lang.reflect.Modifier

/**
 * @author ckreps
 */
trait AbstractClassFilter extends NullFilter
{
  override def accept(testClass:Class[_]):Boolean =
  {
    !Modifier.isAbstract(testClass.getModifiers) && super.accept(testClass)
  }
}