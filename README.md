# scala-macro-tools [![Build](https://github.com/jxnu-liguobin/scala-macro-tools/actions/workflows/ScalaCI.yml/badge.svg)](https://github.com/jxnu-liguobin/scala-macro-tools/actions/workflows/ScalaCI.yml)

Motivation
--

scala macro and abstract syntax tree learning code.

# Features

## @toString

- Note
    - `verbose` Whether to enable detailed log.
    - `withFieldName` Whether to include the name of the field in the toString.
    - `withInternalField` Whether to include the fields defined within a class.
    - Support `case class` and `class`.

- Example

```scala
class TestClass(val i: Int = 0, var j: Int) {
  val y: Int = 0
  var z: String = "hello"
  var x: String = "world"
}

println(new TestClass(1, 2));
```

|withInternalField / withFieldName| false  |true
|  ---------------------------------  | ----------------------------------  |----------------------------------|
|false|```TestClass(1, 2)``` |```TestClass(i=0, j=2)```|
|true|```TestClass(1, 2, 0, hello, world)```|```TestClass(i=1, j=2, y=0, z=hello, x=world)```|

# How to use

Add library dependency
```scala
"io.github.jxnu-liguobin" %% "scala-macro-tools" % "<VERSION>"
```


The artefacts have been uploaded to Maven Central.

| Library Version | Scala 2.11 | Scala 2.12 | Scala 2.13 |
|---------|------------|------------|------------|
|0.0.1|-|-|[![Maven Central](https://img.shields.io/maven-central/v/io.github.jxnu-liguobin/scala-macro-tools_2.13.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.jxnu-liguobin%22%20AND%20a:%22scala-macro-tools_2.13%22)|

Importing the library into your build system (e.g gradle, sbt), is not enough. You need to follow an extra step.

| Scala 2.11 | Scala 2.12 | Scala 2.13 |
|------------|-------------|------------|
| Import macro paradise plugin  | Import macro paradise plugin | Enable compiler flag `-Ymacro-annotations` required |

```scala
addCompilerPlugin("org.scalamacros" % "paradise_<your-scala-version>" % "<plugin-version>")
```

Where `<your-scala-version>` must be the full scala version. For example 2.12.13, and not 2.12.

If that doesn't work, google for alternatives.

In version scala`2.13.x`, the functionality of macro paradise has been included in the scala compiler directly. 
However, you must still enable the compiler flag `-Ymacro-annotations`. Please see examples from `examples212` sub-project.