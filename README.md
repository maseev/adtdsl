ADT DSL
=====
[![Build Status](https://travis-ci.org/maseev/adtdsl.svg?branch=master)](https://travis-ci.org/maseev/adtdsl)
[![GitHub](https://img.shields.io/github/license/maseev/adtdsl.svg)](https://github.com/maseev/adtdsl/blob/master/LICENSE)

DSL for your ADTs
-------------------------------------------------------------------------------------------
`adtdsl` lets yout define your ADTs using the concise syntax of an internal DSL. 
Here's a little example of what you can do with it:

```scala
adt MyList {
  object Nil
  class Cons(head: Int, tail: MyList)
}
```

transforms into

```scala
sealed trait MyList

object MyList {
  case object Nil extends MyList
  case class Cons(head: Int, tail: MyList) extends MyList
}
```

How to build
------------
* Clone this repository
* Run `./sbt publishLocal` in the project folder to build the project and install it to the local repository

How to use
----------

Add `adtdsl` library as your project dependency:

##### SBT
```scala
libraryDependencies += "io.github.maseev" % "adtdsl" % "0.1"
```

##### API

```scala
import com.adtdsl.AdtGenerator._

val input =
  """
    |adt MyList {
    |  object Nil
    |  class Cons(head: Int, tail: MyList)
    |}
    |""".stripMargin

val scalaCode = generate(input)
```