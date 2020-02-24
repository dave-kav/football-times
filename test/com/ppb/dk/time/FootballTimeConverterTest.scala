package com.ppb.dk.time

import org.junit.Test
import org.junit.Assert

class FootballTimeConverterTest {

  private val validTestCases = Map[String, String](
    "[PM] 0:00.000" -> "00:00 - PRE_MATCH",
    "[H1] 0:15.025"   -> "00:15 - FIRST_HALF",
    "[H1] 3:07.513" 	-> "03:08 - FIRST_HALF",
    "[H1] 45:00.001"  -> "45:00 +00:00 - FIRST_HALF",
    "[H1] 46:15.752"  -> "45:00 +01:16 - FIRST_HALF",
    "[HT] 45:00.000"  -> "45:00 - HALF_TIME",
    "[H2] 45:00.500"  -> "45:01 - SECOND_HALF",
    "[H2] 90:00.908"  -> "90:00 +00:01 - SECOND_HALF",
    "[FT] 90:00.000"  -> "90:00 +00:00 - FULL_TIME"
  )

  private val invalidTestCases = Seq(
    "90:00",
    "[H3] 90:00.000",
    "[PM] -10:00.000",
    "FOO"
  )

  private val testClass: StringConverter = new FootballTimeConverter

  @Test
  def testValidConversions(): Unit = {
    validTestCases.foreach { testCase =>
      val expected = testCase._2
      val actual = testClass.convert(testCase._1)
      Assert.assertEquals(s"Expected $expected but received $actual. Input ${testCase._1}", expected, actual)
    }
  }

  @Test
  def testInvalidCases(): Unit = {
    invalidTestCases.foreach { testCase =>
      val result = testClass.convert(testCase)
      Assert.assertEquals(Converter.Constants.Invalid, result)
    }
  }

}
