package forms

import play.api.data.Form
import play.api.data.Forms.{mapping, text}

object TimeForm {

  val timeForm = Form(
    mapping(
      "time" -> text
    )(TimeData.apply)(TimeData.unapply)
  )
}

case class TimeData(time: String)
