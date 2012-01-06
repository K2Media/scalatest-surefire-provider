package com.theladders.platform.scalatestprovider

import org.scalatest.events._
import org.apache.maven.surefire.report.RunListener
import com.theladders.platform.com.theladders.platform.scalatestprovider.ConcurrentEventQueue

/**
 * Receives ScalaTest Event instances during test
 * execution from any number of ReportingDelegate
 * instances.
 *
 * This class is only needed to provide a
 * singleton module to tie all the ReportingDelegates
 * created by ScalaTest (which can't be singletons).
 * This lets us aggregate every event.
 *
 * @author ckreps
 */
object ReportBridge
{
  private val eventQueue = ConcurrentEventQueue()

  def flushTo(listener:RunListener)
  {
    eventQueue.flushTo(listener)
  }

  def add(event:Event)
  {
    eventQueue.add(event)
  }
}