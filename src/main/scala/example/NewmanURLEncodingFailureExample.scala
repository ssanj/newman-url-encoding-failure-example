package example

import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import java.net.URL
import java.net.URI
import com.twitter.util.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object NewmanURLEncodingFailureExample extends App {


  val url = new URL("http://fonts.googleapis.com/css?family=Cabin+Sketch:700|Duru+Sans")

  private def encodeURL(url:URL): URL = {
    //create a URI from the URL to properly encode any special characters. Then extract a new URL
    new URI(url.getProtocol, url.getHost, url.getPath, url.getQuery, null).toURL
  }

  def access(url:URL, clientName:String)(implicit client:HttpClient) {
    try {
      val f = HEAD(url).apply

      val response = Await.result(f, 5.second)
      println(s"Request for $url by $clientName returned with code ${response.code}")
    }
    catch {
      case t:Throwable => println(s"Request for $url for $clientName failed with: ${t.getStackTrace.mkString(System.getProperty("line.separator"))}")
    }
  }

  def performAccessWith(client: => HttpClient, clientName:String) {
    implicit val httpClient = client

    access(encodeURL(url), s"$clientName [with fix]")
    access(url, s"$clientName [without fix]")

    println()
  }

  performAccessWith(new FinagleHttpClient(requestTimeout = Duration.fromSeconds(5)), "FinagleHttpClient")
  performAccessWith(new ApacheHttpClient ,"ApacheHttpClient")

}