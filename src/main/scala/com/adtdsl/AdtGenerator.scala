package com.adtdsl

import com.adtdsl.model.{ClassType, DownType, ObjectType, Parameter, ReferenceType, RootType}

import scala.annotation.tailrec

class AdtGenerator {

  def generate(rootType: RootType): String = {
    validate(rootType)

    s"""
       |sealed trait ${rootType.name}
       |
       |object ${rootType.name} {
       |${generate(rootType.name, rootType.downTypes)}
       |}
       |""".stripMargin
  }

  private def validate(rootType: RootType): Unit = {
    val uniqueTypes = rootType.downTypes.map(_.name).toSet

    if (uniqueTypes.size < rootType.downTypes.size) {
      throw new IllegalArgumentException(s"Duplicate type definitions are not allowed; " +
        s"declared types: $uniqueTypes")
    }

    val classTypes = rootType.downTypes.filter(_.getClass == classOf[ClassType])

    classTypes.foreach {
      case ClassType(name, parameters) =>
        val uniqueParameterNames = parameters.map(_.name).toSet

        if (uniqueParameterNames.size < parameters.size) {
          throw new IllegalArgumentException(s"Duplicate parameter names are not allowed; " +
            s"type name: $name; type parameters: $uniqueParameterNames")
        }

        val referenceParameterTypes =
          parameters.map(_.aType).filter(_.getClass == classOf[ReferenceType])
        val availableTypes = uniqueTypes + rootType.name

        if (referenceParameterTypes.exists(parameterType => !availableTypes(parameterType.clazz))) {
          throw new IllegalArgumentException(s"Unknown parameter types are not allowed; " +
            s"declared types: $availableTypes; target type: $name; type parameters: $referenceParameterTypes")
        }
    }
  }

  private def generate(superType: String, downTypes: List[DownType]): String = {
    def mkString(parameters: List[Parameter]): String = {
      @tailrec
      def mkString(parameters: List[Parameter], acc: String): String = parameters match {
        case Nil => acc
        case parameter :: tail =>
          val delimeter = if (acc.isEmpty) "" else ", "
          val parameterCode = parameter.name + ": " + parameter.aType.clazz
          mkString(tail, acc + delimeter + parameterCode)
      }

      mkString(parameters, "")
    }

    @tailrec
    def generate(superType: String, downTypes: List[DownType], code: String): String = {
      if (downTypes.isEmpty) {
        code
      } else {
        val separator = if (downTypes.tail.isEmpty) "" else "\n"
        val typeCode = downTypes.head match {
          case ObjectType(name) => s"  case object $name extends $superType" + separator
          case ClassType(name, parameters) =>
            s"  case class $name(${mkString(parameters)}) extends $superType" + separator
        }

        generate(superType, downTypes.tail, code + typeCode)
      }
    }

    generate(superType, downTypes, "")
  }
}
