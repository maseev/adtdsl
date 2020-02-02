package com.adtdsl

sealed trait Adt {
  val name: String
}

final case class RootType(name: String, downTypes: List[DownType]) extends Adt {
  require(noDuplicateTypes, "Duplicate type definitions are not allowed")
  require(noUnknownParameterTypes, "Unknown parameter types are not allowed")

  private def noDuplicateTypes: Boolean = {
    val uniqueTypes = downTypes.map(_.name).toSet

    uniqueTypes.size == downTypes.size
  }

  private def noUnknownParameterTypes: Boolean = {
    val uniqueTypes = downTypes.map(_.name).toSet
    val availableTypes = uniqueTypes + name

    downTypes.forall {
      case ClassType(_, parameters) =>
        parameters.map(_.aType).forall {
          case ReferenceType(clazz) => availableTypes(clazz)
          case PrimitiveType(_) => true
        }
      case ObjectType(_) => true
    }
  }
}


sealed trait DownType extends Adt

final case class ObjectType(name: String) extends DownType
final case class ClassType(name: String, parameters: List[Parameter]) extends DownType {
  require(noDuplicateParameters, "Duplicate parameter names are not allowed")

  private def noDuplicateParameters: Boolean = {
    val uniqueParameterNames = parameters.map(_.name).toSet

    uniqueParameterNames.size == parameters.size
  }
}


final case class Parameter(name: String, aType: ParameterType)


sealed trait ParameterType {
  def clazz: String
}

final case class PrimitiveType(clazz: String) extends ParameterType
final case class ReferenceType(clazz: String) extends ParameterType