package com.adtdsl

import scala.util.Try

object AdtGenerator {

  def generate(input: String): Try[String] = Try {
    val parser = new AdtParser
    val rootType = parser.parseAll(parser.rootType, input).get

    s"""
       |sealed trait ${rootType.name}
       |
       |object ${rootType.name} {
       |${generate(rootType.name, rootType.downTypes)}
       |}
       |""".stripMargin
  }

  private def generate(superType: String, downTypes: List[DownType]): String = {
    def mkString(parameters: List[Parameter]): String = {
      parameters
        .map(parameter => parameter.name + ": " + parameter.aType.clazz)
        .mkString(", ")
    }

    downTypes.map {
      case ObjectType(name) => s"  case object $name extends $superType"
      case ClassType(name, parameters) =>
        s"  case class $name(${mkString(parameters)}) extends $superType"
    }.mkString("\n")
  }
}
