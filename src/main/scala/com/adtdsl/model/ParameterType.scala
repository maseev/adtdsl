package com.adtdsl.model

sealed trait ParameterType {
  val clazz: String
}
final case class PrimitiveType(clazz: String) extends ParameterType
final case class ReferenceType(clazz: String) extends ParameterType