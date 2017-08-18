package controllers

import javax.inject._

import play.api.{ Configuration, Logger }
import play.api.libs.json.{ JsObject, JsString, JsValue, Json }
import play.api.libs.ws._
import play.api.mvc._

import java.io.PrintStream

import scala.collection.immutable.SortedMap
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import net.ruippeixotog.scalascraper.browser.{ HtmlUnitBrowser, JsoupBrowser }
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.HtmlValidator
import net.ruippeixotog.scalascraper.util.EitherRightBias._
import net.ruippeixotog.scalascraper.util.ProxyUtils

import scala.concurrent.{ ExecutionContext, Future }
import org.mongodb.scala.bson.collection.immutable.Document
import dao.MongoDAO
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import java.util.Calendar
import java.text.SimpleDateFormat
import org.bson.BsonString
import org.bson.BsonDocument
import org.bson.conversions.Bson
import service.ingestionservice

@Singleton
class StockController @Inject() (ws: WSClient,
                                configuration: Configuration,
                                cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  private val logger = Logger(this.getClass)

  private val sentimentUrl = configuration.get[String]("sentiment.url")

  private val tweetUrl = configuration.get[String]("tweet.url")

  case class Tweet(text: String)

  //private implicit val tweetReads = Json.reads[Tweet]

  def ingestData() = Action {
    ingestionservice.ingestData("http://money.rediff.com/gainers/bse/weekly", "bse", "gainers", "weekly")
    ingestionservice.ingestData("http://money.rediff.com/gainers/bse/daily", "bse", "gainers", "daily")
    ingestionservice.ingestData("http://money.rediff.com/gainers/bse/monthly", "bse", "gainers", "monthly")

    ingestionservice.ingestData("http://money.rediff.com/losers/bse/weekly", "bse", "losers", "weekly")
    ingestionservice.ingestData("http://money.rediff.com/losers/bse/daily", "bse", "losers", "daily")
    ingestionservice.ingestData("http://money.rediff.com/losers/bse/monthly", "bse", "losers", "monthly")

    Ok("Done")

  }
  
  def getStockHistory(stockName:String , MCStockID: String)= Action{
   ingestionservice.ingestStockHistory(stockName,MCStockID, "bse")
   ingestionservice.ingestStockHistory(stockName,MCStockID, "nse")
    
    Ok("Stock History for "+stockName)
  }
  
  
  def setup()=Action{
    
    MongoDAO.initializeDB
    
    Ok("Setup Completed")
  }
  
  


  private def getTextSentiment(text: String): Future[WSResponse] = {
    logger.info(s"getTextSentiment: text = $text")

    ws.url("http://www.moneycontrol.com/stocks/company_info/js/charting/holidays.json").get()

    // ws.url(sentimentUrl).post(Map("text" -> Seq(text)))
  }

  private def getAverageSentiment(responses: Seq[WSResponse], label: String): Double = {
    responses.map { response =>
      (response.json \\ label).head.as[Double]
    }.sum / responses.length.max(1)
  } // avoid division by zero

  /*private def loadSentimentFromTweets(json: JsValue): Seq[Future[WSResponse]] = {
    (json \ "statuses").as[Seq[Tweet]] map (tweet => getTextSentiment(tweet.text))
  }*/

  private def getTweets(symbol: String): Future[WSResponse] = {
    logger.info(s"getTweets: symbol = $symbol")

    ws.url(tweetUrl.format(symbol)).get.withFilter { response =>
      response.status == OK
    }
  }

  private def sentimentJson(sentiments: Seq[WSResponse]): JsObject = {
    logger.info(s"sentimentJson: sentiments = $sentiments")

    val neg = getAverageSentiment(sentiments, "neg")
    val neutral = getAverageSentiment(sentiments, "neutral")
    val pos = getAverageSentiment(sentiments, "pos")

    val response = Json.obj(
      "probability" -> Json.obj(
        "neg" -> neg,
        "neutral" -> neutral,
        "pos" -> pos))

    val classification =
      if (neutral > 0.5)
        "neutral"
      else if (neg > pos)
        "neg"
      else
        "pos"

    val r = response + ("label" -> JsString(classification))
    logger.info(s"response = $r")

    r
  }

}
