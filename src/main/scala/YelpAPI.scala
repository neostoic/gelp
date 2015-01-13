import org.scribe.builder.ServiceBuilder
import org.scribe.model._
import org.json4s._
import org.json4s.native.JsonMethods._

object YelpAPI {

  private val CONSUMER_KEY = ""
  private val CONSUMER_SECRET = ""
  private val TOKEN = ""
  private val TOKEN_SECRET = ""
  private val YELP_SEARCH_URL = "http://api.yelp.com/v2/search"

  private lazy val service = new ServiceBuilder()
    .provider(classOf[TwoStepOAuth])
    .apiKey(CONSUMER_KEY)
    .apiSecret(CONSUMER_SECRET)
    .build()
  private lazy val accessToken = new Token(TOKEN, TOKEN_SECRET)

  case class Coordinate(latitude: Double, longitude: Double)
  case class Location(postal_code: String, address: List[String], coordinate: Coordinate)
  case class Business(id: String, name: String, rating: Double, review_count: BigInt, phone: Option[String], location: Location) {
    def toDisplayString =
      s"""
         |ID: $id
         |Name: $name
         |Rating: $rating
         |Reviews: $review_count
         |Phone: ${phone.getOrElse("N/A")}
       """.stripMargin
  }

  def runYelpSearch() {
    println("Let's get this Yelp party started!")

    val jsonResp = send(s"$YELP_SEARCH_URL?$yelpQueryParams")

    implicit val formats = DefaultFormats
    val allBusinessResults = (jsonResp \ "businesses").extract[List[Business]]

    allBusinessResults.map(_.toDisplayString).foreach(println)
  }

  def send(requestURL: String) = {
    val request = new OAuthRequest(Verb.GET, requestURL)

    println(s"\nQuerying ${ request.getCompleteUrl } ...")

    service.signRequest(accessToken, request)
    val rawResp = request.send()
    val jsonResp = parse(rawResp.getBody)

    println(if (rawResp.isSuccessful) "Success!" else "Failure!")
    println(pretty(render(jsonResp)))

    jsonResp
  }

  def yelpQueryParams = Map(
    "ll" -> "37.776472,-122.437833", // The Mill
    "radius_filter" -> "30"
  ).map({ case(key, value) => s"$key=$value" }).mkString("&")
}
