package dao

import org.mongodb.scala._
import com.mongodb.client.result.UpdateResult
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import com.mongodb.client.model.UpdateOptions
import java.util.Date
import org.bson.Document;

import com.mongodb.client.model.IndexOptions
import org.mongodb.scala.model.Indexes._



object MongoDAO {

  // To directly connect to the default server localhost on port 27017
 // val mongoClient: MongoClient = MongoClient("mongodb://192.168.0.13")
  
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("stocks")

  
  
  def initializeDB{
    val indexOptions = new IndexOptions().unique(true);
    
   /* database.getCollection("stockdata").createIndex(Indexes.compoundIndex(Indexes.ascending("company","date","exchange","period","resultType")),indexOptions)
    database.getCollection("histdata").createIndex(Indexes.compoundIndex(Indexes.ascending("company","date","exchange")),indexOptions)*/
   

     val observable: Observable[String] =database.getCollection("stockdata").createIndex(compoundIndex(ascending("company", "date", "exchange", "period", "resultType")),indexOptions)
     
      observable.subscribe(new Observer[String] {

      override def onNext(result: String): Unit = println("compoundIndex created for stockdata")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("compoundIndex creation for stockdata Completed")
    })
    
    
    
    val observable1: Observable[String] =database.getCollection("histdata").createIndex(compoundIndex(ascending("company", "date", "exchange")),indexOptions)
     
      observable1.subscribe(new Observer[String] {

      override def onNext(result: String): Unit = println("compoundIndex created for histdata")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("compoundIndex creation for histdata Completed")
    })
    
    
    
  }

  
  def saveDataToCollection(collectionName: String, doc: Document) {
    
    val collection: MongoCollection[Document] = database.getCollection(collectionName);

    val observable: Observable[Completed] = collection.insertOne(doc)

    // Explictly subscribe:
    observable.subscribe(new Observer[Completed] {

      override def onNext(result: Completed): Unit = println("Inserted")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }

 /* def upsertData(id:String, update: Document) = {

    val updateOptions = new UpdateOptions().upsert(true)
    
    
    collection.updateOne(Filters.eq("_id", id),update,updateOptions).subscribe((updateResult: UpdateResult) => println(updateResult))

  }

  def findData(filter: Document): String = {
    var result = ""

    collection.find(filter).collect().subscribe((results: Seq[Document]) =>
      {

        println(s"Found: #${results.size}")

        result = results.size.toString()
      })

    result

  }*/

}