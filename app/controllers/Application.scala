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
  
  

 
  def lineChart = Action { implicit request=>
    Ok(views.html.lineChart())
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