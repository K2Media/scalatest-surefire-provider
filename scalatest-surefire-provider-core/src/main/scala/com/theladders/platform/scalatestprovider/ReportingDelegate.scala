package com.theladders.platform.scalatestprovider

import org.scalatest.Reporter
import org.scalatest.events.Event

/**
 * @author ckreps
 */
class ReportingDelegate extends Reporter
{
  def apply(event:Event)
  {
    ReportBridge.add(event)
  }
}