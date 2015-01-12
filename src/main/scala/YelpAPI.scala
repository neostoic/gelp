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
    val service = new ServiceBuilder()
      .provider(classOf[TwoStepOAuth])
      .apiKey(CONSUMER_KEY)
      .apiSecret(CONSUMER_SECRET)
      .build()

    val accessToken = new Token(TOKEN, TOKEN_SECRET)
    val request = new OAuthRequest(Verb.GET, s"$YELP_SEARCH_URL?$yelpQueryParams")

    println(s"Querying ${ request.getCompleteUrl } ...")
    service.signRequest(accessToken, request)
    val response = request.send()
    println(if (response.isSuccessful) "Success!" else "Failure!")

    val jsonResp = parse(response.getBody)

    implicit val formats = DefaultFormats
    val allBusinessResults = (jsonResp \ "businesses").extract[List[Business]]

    allBusinessResults.map(_.toDisplayString).foreach(println)
    println(pretty(render(jsonResp)))
  }

  def yelpQueryParams = Map(
    "ll" -> "37.776472,-122.437833", // The Mill
    "radius_filter" -> "30"
  ).map({ case(key, value) => s"$key=$value" }).mkString("&")
}
