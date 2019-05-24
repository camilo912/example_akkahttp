import com.mongodb.MongoCredential
import org.mongodb.scala.bson.collection.mutable.Document
import org.mongodb.scala.{Completed, FindObservable, MongoClient, MongoClientSettings, MongoCollection, MongoDatabase, Observable, Observer, ReadPreference, ServerAddress}
import org.mongodb.scala.connection.ClusterSettings
import com.mongodb.MongoCredential._
import java.util.logging.{Level, Logger}

import org.mongodb.scala.connection.{NettyStreamFactoryFactory, SslSettings}
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.UpdateResult

import scala.collection.JavaConverters._
object newworld {
  def main(args: Array[String]): Unit = {val mongoLogger: Logger = Logger.getLogger("com.mongodb")
    mongoLogger.setLevel(Level.SEVERE);
    /*val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(List(new ServerAddress("example.com:27345"), new ServerAddress("example.com:20026")).asJava).build()
    val user: String = "testuser"
    val databasename: String = "scalatest"
    val password: Array[Char] = "<enter-a-password>".toCharArray
    val credential: MongoCredential = createCredential(user, databasename, password)
    val settings: MongoClientSettings = MongoClientSettings.builder()
      .clusterSettings(clusterSettings).credentialList(List(credential,credential).asJava).sslSettings(SslSettings.builder().enabled(true).build())
      .streamFactoryFactory(NettyStreamFactoryFactory()).build()*/

    val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(List(new ServerAddress("localhost")).asJava).build()
    val settings: MongoClientSettings = MongoClientSettings.builder().clusterSettings(clusterSettings).build()
    val mongoClient: MongoClient = MongoClient(settings)

    val database: MongoDatabase = mongoClient.getDatabase("mydb");
    /*val mongoClient: MongoClient = MongoClient(settings)
    val database: MongoDatabase = mongoClient.getDatabase("scalatest")*/
    val collection: MongoCollection[Document] = database.getCollection("mycoll")

    val document: Document = Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
      "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

    // insert a document
    //val document: Document = Document("_id" -> 1, "x" -> 1)
    val insertObservable: Observable[Completed] = collection.insertOne(document)

    insertObservable.subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println(s"onNext: $result")
      override def onError(e: Throwable): Unit = println(s"onError: $e")
      override def onComplete(): Unit = println("onComplete")
    })

    val replacementDoc: Document = Document("_id" -> 1, "x" -> 2, "y" -> 3)

    // replace a document
    collection.replaceOne(Filters.eq("_id", 1), replacementDoc
    ).subscribe((updateResult: UpdateResult) => println(updateResult))

    // find documents
    collection.find().collect().subscribe((results: Seq[Document]) => println(s"Found: #${results.size}"))

    mongoClient.close()
  }
}