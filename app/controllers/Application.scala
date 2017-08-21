package controllers

import javax.inject._

import play.api.{ Configuration, Logger }
import play.api.libs.json.{ JsObject, JsString, JsValue, Json }
import play.api.libs.ws._
import play.api.mvc._
import play.api.i18n._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class Application @Inject()(component: ControllerComponents,
                                    langs: Langs) extends AbstractController(component) with I18nSupport  {
  
  val lang: Lang = langs.availables.head
  implicit val messages: Messages = MessagesImpl(lang, messagesApi)

  def index = Action { implicit request =>
    //val messages: Messages = messagesApi.preferred(request)   // get the messages for the given request
    //val message: String = messages("default.message")
    
    Ok(views.html.lineChart(Messages("subheader.time_line")))
  }

  def lineChart = Action { implicit request=>
    Ok(views.html.lineChart(Messages("subheader.time_line")))
  }

  /*  def columnAndBarChart = Action {
    Ok(views.html.columnAndBar(Messages("subheader.bar_column")))
  }

  def areaAndPieChart = Action {
    Ok(views.html.pieAndArea(Messages("subheader.area_pie")))
  }

  def bubbleChart = Action {
    Ok(views.html.bubble(Messages("subheader.bubble")))
  }
*/
}