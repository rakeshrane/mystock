package service

import java.util.Calendar
import java.text.SimpleDateFormat

import net.ruippeixotog.scalascraper.browser.{ HtmlUnitBrowser, JsoupBrowser }
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.HtmlValidator
import net.ruippeixotog.scalascraper.util.EitherRightBias._
import net.ruippeixotog.scalascraper.util.ProxyUtils

import org.mongodb.scala.bson.collection.immutable.Document
import dao.MongoDAO
import org.bson.BsonDecimal128
import scala.io.Source
import java.sql.Date
import util.DateUtil

object ingestionservice {

  def ingestData(uri: String, exchange: String, resultType: String, period: String) {
    val browser = HtmlUnitBrowser.typed()
    val doc = browser.get(uri)
    val now = Calendar.getInstance().getTime()
    val items = doc >> elementList(".dataTable tr")

    items.map(element => {
      val tds = element.children
      val tdarray = new Array[Element](5)
      tds.copyToArray(tdarray)
      if (!tdarray(0).tagName.equalsIgnoreCase("th")) {

        val company = tdarray(0).text
        
        
        val prevclose = (tdarray(2).text).filterNot(char => char == ',').toDouble
        val currentprice = (tdarray(3).text).filterNot(char => char == ',').toDouble
        val changetype = (tdarray(4).text).filterNot(char => char == ' ').take(1)
        val change = (tdarray(4).text).filterNot(char => char == ' ').substring(1).toDouble

        val doc: Document = Document("company" -> company, "date" -> DateUtil.removeTime(now), "group" -> tdarray(1).text, "prevclose" -> prevclose,
          "currentprice" -> currentprice, "changetype" -> changetype, "change" -> change, "period" -> period, "resultType" -> resultType, "exchange" -> exchange)
        MongoDAO.saveDataToCollection("stockdata", doc)
      }

    })
  }

  def ingestStockHistory(StockName: String, MCStockID: String, exchange: String) {

    val bufferedSource = Source.fromURL("http://www.moneycontrol.com/tech_charts/bse/his/" + MCStockID + ".csv")

    for (line <- bufferedSource.getLines) {
      val cols = line.split(",").map(_.trim)
      val doc: Document = Document("company" -> StockName, "date" -> cols(0), "openprice" -> cols(1).toDouble, "highprice" -> cols(2).toDouble, "lowprice" -> cols(3).toDouble, "closeprice" -> cols(4).toDouble, "volume" -> cols(5).toInt, "exchange" -> exchange)
      MongoDAO.saveDataToCollection("histdata", doc)
    }
    bufferedSource.close

  }

}