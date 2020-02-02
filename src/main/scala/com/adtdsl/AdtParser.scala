package com.adtdsl

import scala.util.parsing.combinator._

class AdtParser extends JavaTokenParsers {
  def rootType: Parser[RootType] = "adt" ~> ident ~ "{" ~ rep1(downType) ~ "}" ^^ {
    case adtType ~ "{" ~ downTypes ~ "}" => RootType(adtType, downTypes)
  }
  def downType: Parser[DownType] = objectType | classType
  def objectType: Parser[ObjectType] = "object " ~> ident ^^ ObjectType
  def classType: Parser[ClassType] = "class " ~> ident ~ "(" ~ rep1sep(parameter, ",") ~ ")" ^^ {
    case name ~ "(" ~ parameters ~ ")" => ClassType(name, parameters)
  }
  def parameter: Parser[Parameter] = ident ~ ":" ~ parameterType ^^ {
    case name ~ ":" ~ aType => Parameter(name, aType)
  }
  def parameterType: Parser[ParameterType] = primitiveType | referenceType
  def referenceType: Parser[ReferenceType] = ident ^^ ReferenceType
  def primitiveType: Parser[PrimitiveType] =
    ("Boolean" |
    "Byte"     |
    "Short"    |
    "Int"      |
    "Long"     |
    "Float"    |
    "Double"   |
    "Char"     |
    "String")  ^^ PrimitiveType
}






