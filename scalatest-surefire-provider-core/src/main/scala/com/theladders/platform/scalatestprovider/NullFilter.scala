package com.theladders.platform.scalatestprovider

import org.apache.maven.surefire.util.ScannerFilter

/**
 * @author ckreps
 */
class NullFilter extends ScannerFilter
{
  def accept(testClass:Class[_]):Boolean = testClass != null
}