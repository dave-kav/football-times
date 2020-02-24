package com.ppb.dk.time

import com.google.inject.ImplementedBy

trait Converter[T] {
  def convert(input: T): String
}

@ImplementedBy(classOf[FootballTimeConverter])
abstract class StringConverter extends Converter[String]

object Converter {
  object Constants {
    val Invalid = "INVALID"
  }
}