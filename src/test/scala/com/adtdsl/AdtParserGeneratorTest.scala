package com.adtdsl

import org.scalatest.FunSuite

import CodeGenerator._

class AdtParserGeneratorTest extends FunSuite {

  test("duplicate types are not allowed") {
    val input =
      """
        |adt MyList {
        |  object Nil
        |  object Nil
        |}
        |""".stripMargin

    assertThrows[IllegalArgumentException](generate(input))
  }

  test("duplicate parameter names are not allowed") {
    val input =
      """
        |adt MyList {
        |  class Cons(head: Int, head: Int)
        |}
        |""".stripMargin

    assertThrows[IllegalArgumentException](generate(input))
  }

  test("unknown parameter types are not allowed") {
    val input =
      """
        |adt MyList {
        |  class Cons(head: UnknownType)
        |}
        |""".stripMargin

    assertThrows[IllegalArgumentException](generate(input))
  }

  test("forward reference should be allowed") {
    val input =
      """
        |adt MyList {
        |  class Cons(head: Int, tail: Nil)
        |  object Nil
        |}
        |""".stripMargin
    val expectedCode =
      """
        |sealed trait MyList
        |
        |object MyList {
        |  case class Cons(head: Int, tail: Nil) extends MyList
        |  case object Nil extends MyList
        |}
        |""".stripMargin

    assertResult(expectedCode)(generate(input))
  }

  test("fully-compliant adt description should generate the valid Scala code") {
    val input =
      """
        |adt MyList {
        |  object Nil
        |  class Cons(head: Int, tail: MyList)
        |}
        |""".stripMargin
    val expectedCode =
      """
        |sealed trait MyList
        |
        |object MyList {
        |  case object Nil extends MyList
        |  case class Cons(head: Int, tail: MyList) extends MyList
        |}
        |""".stripMargin

    assertResult(expectedCode)(generate(input))
  }
}
