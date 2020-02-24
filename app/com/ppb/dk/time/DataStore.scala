package com.ppb.dk.time

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import play.api.Logger

import scala.collection.mutable

@ImplementedBy(classOf[TimeStore])
trait DataStore[T] {
  def add(item: T): Unit
  def listAll: Seq[T]
  def clear(): Unit
}

@Singleton
class TimeStore extends DataStore[String] {

  private val logger = Logger(this.getClass)

  private val times: mutable.ListBuffer[String] = mutable.ListBuffer[String]()

  override def add(item: String): Unit = {
    times += item
    logger.info(s"Stored [$item] to ${this.getClass.getName}")
  }

  override def listAll: Seq[String] = times.toSeq

  override def clear(): Unit = {
    times.clear()
    logger.info(s"${this.getClass.getName} cleared.")
  }
}
