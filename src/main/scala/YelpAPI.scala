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

  case class YelpBusiness(
    name: String,
    rating: Double,
    reviewCount: BigInt,
    phone: String,
    address: List[String],
    coordinate: (Double, Double)
  )

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

    // JSON Documentation: https://github.com/json4s/json4s
    val jsonResp = parse(response.getBody)

    val allResults: List[YelpBusiness] = for {
      JArray(businesses) <- jsonResp \\ "businesses"
      JObject(business) <- businesses
      JField("name", JString(name)) <- business
      JField("rating", JDouble(rating)) <- business
      JField("review_count", JInt(reviewCount)) <- business
      JField("phone", JString(phone)) <- business
      JField("location", JObject(location)) <- business
      JField("address", JArray(address)) <- location
      JField("coordinate", JObject(coordinate)) <- location
      JField("latitude", JDouble(latitude)) <- coordinate
      JField("longitude", JDouble(longitude)) <- coordinate
    } yield YelpBusiness(name, rating, reviewCount, phone, address.map(_.toString), (latitude, longitude))

    allResults.foreach(println)
    println(pretty(render(jsonResp)))
  }

  def yelpQueryParams = Map(
    "ll" -> "37.776472,-122.437833", // The Mill
    "radius_filter" -> "30"
  ).map({ case(key, value) => s"$key=$value" }).mkString("&")
}
