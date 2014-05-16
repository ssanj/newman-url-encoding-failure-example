package example


object Fail extends App {

  import com.stackmob.newman._
  import com.stackmob.newman.dsl._
  import scala.concurrent._
  import scala.concurrent.duration._
  import java.net.URL
  import java.net.URI
  import com.twitter.util.Duration


  val url = new URL("http://fonts.googleapis.com/css?family=Cabin+Sketch:700|Duru+Sans")

  private def encodeURL(url:URL): URL = {
    //create a URI from the URL to properly encode any special characters. Then extract a new URL
    new URI(url.getProtocol, url.getHost, url.getPath, url.getQuery, null).toURL
  }

  def access(url:URL, clientName:String)(implicit client:HttpClient) {

    println(s"Accessing $url from $clientName")
    val response = Await.result(HEAD(url).apply, 5.second)
    println(s"Response returned from ${url.toString} with code ${response.code}, body ${response.bodyString}")
  }

  def performAccessWith(client: => HttpClient, name:String) {
    implicit val httpClient = client
    access(encodeURL(url), s"$name [with fix]")

    try {
      access(url, s"$name [without fix]")
    } catch {
      case x:Throwable => x.printStackTrace()
    }

    println()
  }

  performAccessWith(new ApacheHttpClient ,"ApacheHttpClient")
  performAccessWith(new FinagleHttpClient(requestTimeout = Duration.fromSeconds(5)), "FinagleHttpClient")

}