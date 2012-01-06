## In short...
This tool lets you use ScalaTest to execute JUnit/ScalaTest tests as part of a Maven build.

It is implemented as a surefire provider for the popular surefire test running plugin for the Maven build system.

## What is ScalaTest?
[ScalaTest](http://http://www.scalatest.org/) is an excellent testing framework for the JVM.

## What is a surefire provider?
The Maven build tool uses a plugin called "surefire" for running tests.  On it's own surefire knows nothing about
any one test framework.  It's job is to identify tests to be run and then report on the results.  Surefire relies
on "providers" to run the actual tests.  It ships with providers designed for running TestNG and JUnit tests.  Recently
the folks working on surefire released a version allowing 3rd parties to implement their own providers.  This project is
a stab at creating a provider that uses ScalaTest as the test runner.

Integrating ScalaTest with Maven this way is clean and gives you some low level control over test
execution and the Maven build that would be hard if this was just a regular Maven plugin.

This provider is designed to be a drop-in replacement.  This means you can configure it the same way
(except for 1 or 2 key differences) as you configure the JUnit/TestNG providers.  The default settings
are probably sufficient for most people.

## What will this do for me?

- You can run tests for a Maven project containing any number of JUnit/ScalaTest tests.  Tests can be written
  in Java or Scala.  So if you already have a Java project with JUnit tests you can use it to run your test suites.  Though
  that would be kind of pointless if you don't plan on then writing some ScalaTest tests.

- Since you can mix your tests up this might be a good fit for teams looking for a clean migration path to a more modern TDD, BDD
  testing framework.

- Your test output will read better than your existing Maven test output.

- Gives you access in Maven to all the great stuff ScalaTest comes with (pretty output, a bunch of nice DSLs, the ability to write
  more expressive and compact tests in the Scala programming language).

## Quickstart
You need to add scalatest-surefire-provider as both a dependency and as a provider to the surefire plugin (2 places).

The dependency will be something like this:

    <dependency>
      <groupId>com.theladders.platform</groupId>
      <artifactId>scalatest-surefire-provider</artifactId>
      <version>1.0</version>
    </dependency>

The provider config will be something like this (make sure you use maven-surefire-plugin version 2.11+):

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.11</version>
      <dependencies>
        <dependency>
          <groupId>com.theladders.platform</groupId>
          <artifactId>scalatest-surefire-provider</artifactId>
          <version>1.0</version>
        </dependency>
      </dependencies>
    </plugin>

Most Maven projects that use Scala will want to use some other plugins too. If you're stuck try
looking at the "scalatest-surefire-provider-tests" project.  That pom.xml uses the plugin.

## About
This project was created to help make ScalaTest part of our builds at [TheLadders](http://www.theladders.com).
This was a good way to introduce Scala in low-risk ways with our test source.
It's allowed us to learn at a gentle pace and prepared us for the production Scala we're writing now.

Copyright 2012 Charles Kreps.
