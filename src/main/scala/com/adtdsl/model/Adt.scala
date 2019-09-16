package com.adtdsl.model

sealed trait Adt {
  val name: String
}
final case class RootType(name: String, downTypes: List[DownType]) extends Adt

sealed trait DownType extends Adt
final case class ObjectType(name: String) extends DownType
final case class ClassType(name: String, parameters: List[Parameter]) extends DownType