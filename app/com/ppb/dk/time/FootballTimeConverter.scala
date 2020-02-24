package com.ppb.dk.time

import scala.util.{Failure, Success, Try}

import play.api.Logger

class FootballTimeConverter extends StringConverter {

  import Converter.Constants.Invalid
  import TimeSection._

  private val logger = Logger(this.getClass)

  private val inputPattern = """\[[A-Z]+\d?\]\s\d{1,2}:\d{2}.\d{3}""".r

  private val firstHalf = "FIRST_HALF"
  private val secondHalf = "SECOND_HALF"
  private val halfLength = 45

  private val timeCodes = Map[String, String](
    "PM" -> "PRE_MATCH",
    "H1" ->	firstHalf,
    "HT" ->	"HALF_TIME",
    "H2" ->	secondHalf,
    "FT" ->	"FULL_TIME"
  )

  override def convert(input: String): String = Try {
    parseTime(inputPattern.findFirstIn(input).get)
  } match {
    case Failure(exception) => logger.error(s"Unable to parse input String[$input]"); Invalid
    case Success(timeSections) => buildOutput(timeSections)
  }

  // I'm certain there's a more elegant way of doing this by leveraging regex but
  // I'll be the first to hold my hands up and admit I don't know enough about regex
  private def parseTime(input: String): TimeSection = {
    val space = input.indexOf(" ")
    val colon = input.indexOf(":")
    val period = input.indexOf(".")

    val timePeriod = input.slice(0 + 1, space - 1)
    val minutes = input.slice(space + 1, colon)
    val seconds = input.slice(colon + 1, period)
    val millis = input.substring(period + 1)

    TimeSection(Period(timePeriod), Minutes(minutes), Seconds(seconds), Millis(millis))
  }

  private def buildOutput(timeSection: TimeSection): String =
    if (!timeCodes.contains(timeSection.period.value)) {
      logger.error(s"Invalid time period[${timeSection.period}]"); Invalid
    }
    else {
      val timePeriod = timeCodes(timeSection.period.value)
      val time: String = formatTime(timeSection)
      s"$time - $timePeriod"
    }

  private def formatTime(timeSection: TimeSection): String = {
    import timeSection._

    val half = timeCodes(period.value)
    val maxTime = if (half.equals(firstHalf)) halfLength else halfLength * 2
    logger.debug(s"Calculated 'maxTime' as $maxTime")

    Try {
      val minutesAsInt = minutes.value.toInt
      val roundedMillis = if (milliseconds.value.toInt < 500) 0 else 1
      val formattedSeconds = formatInt(seconds.value.toInt + roundedMillis)

      if (minutesAsInt >= maxTime) {
        val minutes = formatInt(minutesAsInt - maxTime)
        s"$maxTime:00 +$minutes:$formattedSeconds"
      } else {
        s"${formatInt(minutesAsInt)}:$formattedSeconds"
      }
    }.recoverWith {
      case t: Throwable => logger.info(s"Error occurred while formatting time: $timeSection", t); throw t
    }.getOrElse(Invalid)
  }

  private def formatInt(int: Int): String = "%02d".format(int)
}

case class TimeSection(period: TimeSection.Period,
                       minutes: TimeSection.Minutes,
                       seconds: TimeSection.Seconds,
                       milliseconds: TimeSection.Millis)

case object TimeSection {
  case class Period(value: String) extends AnyVal
  case class Minutes(value: String) extends AnyVal
  case class Seconds(value: String) extends AnyVal
  case class Millis(value: String) extends AnyVal
}
