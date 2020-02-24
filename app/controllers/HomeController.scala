package controllers

import com.ppb.dk.time.{Converter, StringConverter, TimeStore}
import forms.TimeForm
import javax.inject._
import play.api._
import play.api.mvc._

import forms.TimeForm._

@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               converter: StringConverter,
                               timeStore: TimeStore) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  private val logger = Logger(this.getClass)

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    // case for a dedicated access logger really...
    logger.debug("Request recevied for 'HomeController.index()'")
    Ok(views.html.index(timeForm))
  }

  def listTimes(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    logger.debug("Request received for 'HomeController.listTimes()'")
    val times = timeStore.listAll
    val output = if (times.isEmpty) {
      logger.info("Request received for all times, but no times to display.")
      "Nothing to see here!"
    } else times.mkString("\n")
    Ok(output)
  }

  def postTime(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    logger.debug("Request received for 'HomeController.listTimes()'")
    timeForm.bindFromRequest().fold (
      formWithErrors => {
        logger.error(s"Missing parameters: ${formWithErrors.errors.mkString("\n")}")
        BadRequest(views.html.index(formWithErrors))
      },
      formData => {
        val input = formData.time
        val convertedTime = converter.convert(input)
        val msg = s"Attempted conversion of [${input}] - result[$convertedTime]"
        if (!convertedTime.equals(Converter.Constants.Invalid)) {
          logger.debug(msg)
          timeStore.add(convertedTime)
          Ok(s"Time stored in format[$convertedTime]")
        } else {
          logger.error(msg)
          InternalServerError(convertedTime)
        }
      }
    )
  }
}
