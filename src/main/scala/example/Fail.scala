package example

import java.net.URI

object Fail extends App {

  import com.stackmob.newman._
  import com.stackmob.newman.dsl._
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.net.URL


  private def encodeURL(url:URL): URL = {
    //create a URI from the URL to properly encode any special characters. Then extract a new URL
    new URI(url.getProtocol, url.getHost, url.getPath, url.getQuery, null).toURL
  }

  def access(url:URL) {
    implicit val httpClient = new ApacheHttpClient

    val response = Await.result(HEAD(url).apply, 5.second)
    println(s"Response returned from ${url.toString} with code ${response.code}, body ${response.bodyString}")
  }

  val url = new URL("http://fonts.googleapis.com/css?family=Cabin+Sketch:700|Duru+Sans")

  println("this works")
  access(encodeURL(url))

  println("this doesn't")
  access(new URL("http://fonts.googleapis.com/css?family=Cabin+Sketch:700|Duru+Sans"))
}