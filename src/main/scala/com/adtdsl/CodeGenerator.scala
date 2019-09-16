package com.adtdsl

object CodeGenerator {

  def generate(input: String): String = {
    val parser = new AdtParser
    val rootType = parser.parseAll(parser.rootType, input).get
    val generator = new AdtGenerator

    generator.generate(rootType)
  }
}
