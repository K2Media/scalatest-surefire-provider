package com.theladders.platform.com.theladders.platform.scalatestprovider

import java.lang.{Integer => JInteger}
import org.apache.maven.surefire.report.{SafeThrowable, StackTraceWriter, ReportEntry, RunListener}
import java.lang.Integer.MIN_VALUE
import java.io.{PrintWriter, StringWriter}
import java.util.concurrent.ConcurrentLinkedQueue
import collection.JavaConversions._
import org.scalatest.events._
import org.scalatest.junit.JUnitWrapperSuite

/**
 * Provides storage for ScalaTest Events emitted
 * by ScalaTest during test execution and logic to map
 * these events to their equivalent in surefire.
 *
 * @author ckreps
 */
case class ConcurrentEventQueue()
{
  /**
   * It's not completely clear if ScalaTest's Event
   * system results in concurrent Event submission
   * even with parallel execution turned on.  But I think the
   * benefit we gain from being cautions and synchronizing here
   * outweighs the small performance hit.
   */
  private val queue = new ConcurrentLinkedQueue[Event]

  /**
   * Events are added here by the ReportBridge throughout
   * ScalaTests test execution.  The ReportBridge should be
   * the only thing interacting with this method.
   */
  def add(event:Event)
  {
    queue.add(event)
  }

  /**
   * Invokes methods on the given RunListener using all
   * the events produced during test execution.  This method
   * is designed to only be called once ScalaTest has completed
   * running ALL tests.
   *
   * While we could have tried to map ScalaTest's events to RunListener
   * method invocations in "real-time" as the events come in it's not
   * clear we gain anything but increased complexity.  I feel the output
   * is actually cleaner this way since the summary at the end of
   * test execution is more detailed.
   */
  def flushTo(listener:RunListener)
  {
    val orderedEvents = sortByOrdinal(iterableAsScalaIterable(queue).toSeq)

    orderedEvents.foreach
    {
      case TestStarting(ordinal,_,suiteClassName,testName,_,_,_,_,_) => {
        listener.testStarting(entry(suiteClassName, Some(testName), ordinal, None))
      }
      case TestSucceeded(ordinal,_,suiteClassName,testName,duration,_,_,_,_,_) => {
        listener.testSucceeded(entry(suiteClassName, Some(testName), ordinal, duration))
      }
      case TestFailed(ordinal,_,_,suiteClassName,testName,throwable,duration,_,_,_,_,_) => {
        listener.testFailed(entry(suiteClassName, Some(testName), ordinal, duration, throwable))
      }
      case TestIgnored(ordinal,_,suiteClassName,testName,_,_,_,_) => {
        listener.testSkipped(entry(suiteClassName, Some(testName), ordinal, None))
      }
      case TestPending(ordinal,_,suiteClassName,testName,_,_,_,_) => {
        listener.testSkipped(entry(suiteClassName, Some(testName), ordinal, None))
      }
      case SuiteStarting(ordinal,_,suiteClassName,_,_,_,_,_) => {
        listener.testSetStarting(testSetEntry(suiteClassName, ordinal, None))
      }
      case SuiteCompleted(ordinal,_,suiteClassName,duration,_,_,_,_,_) => {
        listener.testSetCompleted(testSetEntry(suiteClassName, ordinal, duration))
      }
      case SuiteAborted(ordinal,_,_,suiteClassName,throwable,duration,_,_,_,_,_) => {
        listener.testError(testSetEntry(suiteClassName, ordinal, duration, throwable))
      }
      // TODO Use these?  It's not clear what their equivalent (if any) is in surefire.
      case e:RunStarting =>
      case e:InfoProvided =>
      case e:RunStopped =>
      case e:RunAborted =>
      case e:RunCompleted =>
    }

    queue.clear
  }

  /**
   * Corrects the order of events received during parallel test
   * execution.  The scaladoc for ScalaTest's Ordinal class should
   * help explain what the code in this method is doing.
   */
  private def sortByOrdinal(events:Seq[Event]):Seq[Event] =
  {
    events.sortWith((left, right) => {
      val padded = left.ordinal.toList.zipAll(right.ordinal.toList, MIN_VALUE, MIN_VALUE)
      padded.find(p => p._1 != p._2) match {
        case Some(pair) => pair._1 < pair._2
        case None => true
      }
    })
  }

  /**
   * Events associated with JUnit all end up with the same class name of "JUnitWrapperSuite".
   * The name we provide surefire is used to create a unique filename for each test.
   * This will cause reports to be overwritten unless changed.
   * We need to make the names unique.  To do this we can use the 2nd Ordinal
   * value.  This value is the same for each event associated with a single JUnit test but unique
   * between tests.  So we append this value to the name.
   */
  private def addUniqueIdForJUnit(source:Option[String], ordinal:Ordinal):String =
  {
    if(source.isDefined && source.get == classOf[JUnitWrapperSuite].getName)
    {
      source.get + ordinal.toList.get(1)
    }
    else
    {
      source.get
    }
  }

  case class Report(source:String,
                    name:Option[String],
                    elapsed:Option[Long],
                    throwable:Option[Throwable]) extends ReportEntry
  {
    def getGroup:String = null

    def getSourceName = source

    def getElapsed:JInteger = elapsed.getOrElse(0L).toInt

    def getStackTraceWriter: StackTraceWriter =
    {
      throwable match {
        case Some(t) => new SimpleStackTraceWriter(t)
        case None => null
      }
    }

    def getName:String = name.getOrElse(null)

    def getMessage = throwable.orNull
  }

  /**
   * Maps events for a set of tests (i.e. A test class) to
   * events that surefire can understand.
   */
  private def testSetEntry(source:Option[String],
                           ordinal:Ordinal,
                           elapsed:Option[Long],
                           throwable:Option[Throwable] = None):ReportEntry =
  {
    val sourceWithId = addUniqueIdForJUnit(source, ordinal)
    Report(sourceWithId, Some(sourceWithId), elapsed, throwable)
  }


  /**
   * Maps events for an individual test method/section to
   * events that surefire can understand.
   */
  private def entry(source:Option[String],
                    testName:Option[String],
                    ordinal:Ordinal,
                    elapsed:Option[Long],
                    throwable:Option[Throwable] = None):ReportEntry =
  {
    val sourceWithId = addUniqueIdForJUnit(source, ordinal)
    Report(sourceWithId, testName, elapsed, throwable)
  }

  /**
   * A surefire StackTraceWriter.  This is used to produce log
   * and reporting output for each test that fails.
   *
   * All test failures produce a Throwable of some kind which
   * is used to produce a stack trace in the format familiar
   * to most.
   */
  private class SimpleStackTraceWriter(throwable:Throwable) extends StackTraceWriter
  {
    private val formattedStackTraceString =
    {
      val stringWriter = new StringWriter()
      val printWriter = new PrintWriter(stringWriter)
      throwable.printStackTrace(printWriter)
      stringWriter.toString
    }

    override def writeTraceToString = formattedStackTraceString

    override def writeTrimmedTraceToString = formattedStackTraceString

    override def getThrowable = new SafeThrowable(throwable)
  }
}
