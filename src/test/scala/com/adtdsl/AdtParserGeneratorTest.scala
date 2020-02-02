package com.adtdsl

import com.adtdsl.AdtGenerator._
import org.scalatest.TryValues._
import org.scalatest.{FunSuite, Matchers}

import scala.util.Try

class AdtParserGeneratorTest extends FunSuite with Matchers {

  test("duplicate types are not allowed") {
    val input =
      """
        |adt MyList {
        |  object Nil
        |  object Nil
        |}
        |""".stripMargin

    assertFailure(generate(input), classOf[IllegalArgumentException])
  }

  test("duplicate parameter names are not allowed") {
    val input =
      """
        |adt MyList {
        |  class Cons(head: Int, head: Int)
        |}
        |""".stripMargin

    assertFailure(generate(input), classOf[IllegalArgumentException])
  }

  test("unknown parameter types are not allowed") {
    val input =
      """
        |adt MyList {
        |  class Cons(head: UnknownType)
        |}
        |""".stripMargin

    assertFailure(generate(input), classOf[IllegalArgumentException])
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

    generate(input).success.value shouldBe expectedCode
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

    generate(input).success.value shouldBe expectedCode
  }

  private def assertFailure[T](tryValue: Try[T], clazz: Class[_ <: Throwable]): Unit = {
    tryValue.failure.exception.getClass shouldBe clazz
  }
}
