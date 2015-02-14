import dispatch._
import Defaults._
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._

object GooglePlacesAPI {

  private val BROWSER_API_KEY = ""
  private val SERVER_API_KEY = ""
  private val NEARBY_SEARCH_URL = url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
  private val PLACE_DETAILS_URL = url("https://maps.googleapis.com/maps/api/place/details/json")

  case class PlaceID(place_id: String)
  case class Geometry(location: Coordinate)
  case class Business(place_id: String, name: String, rating: Double, user_ratings_total: BigInt, price_level: BigInt, formatted_address: String, website: Option[String], formatted_phone_number: Option[String], geometry: Geometry)  {
    def toDisplayString =
      s"""
         |ID: $place_id
         |Name: $name
         |Rating: $rating
         |Reviews: $user_ratings_total
         |Phone: ${formatted_phone_number.getOrElse("N/A")}
       """.stripMargin
  }

  def runGoogleSearch(coord: Coordinate, radius: Int = 50) {
    println("Let's get this Google party started!")

    val response = send(searchRequest(coord, radius))

    implicit val formats = DefaultFormats
    val placeIDs = (response \ "results").extract[List[PlaceID]]
    val placeDetails = placeIDs.map(id => send(detailsRequest(id)))
    val businesses = placeDetails.map(placeDetail => (placeDetail \ "result").extract[Business])

    businesses.map(_.toDisplayString).foreach(println)
    sys.exit()
  }

  def send(request: Req) = {
    val rawResp = Http(request OK as.String)
    val jsonResp = parse(s"""${ rawResp() }""")

    println(s"\nStatus is: ${ (jsonResp \ "status").values }")
    println(pretty(render(jsonResp)))

    jsonResp
  }

  // Documentation: https://developers.google.com/places/documentation/search#PlaceSearchRequests
  def searchRequest(coord: Coordinate, radius: Int) = NEARBY_SEARCH_URL <<? Map(
    "key" -> SERVER_API_KEY,
    "location" -> s"${coord.lat},${coord.long}",
    "radius" -> radius.toString,
    "types" -> "bakery|bar|cafe|food|restaurant"
  )

  // Documentation: https://developers.google.com/places/documentation/details
  def detailsRequest(placeID: PlaceID) = PLACE_DETAILS_URL <<? detailsQueryParams(placeID)
  def detailsQueryParams(placeID: PlaceID) = Map(
    "key" -> SERVER_API_KEY,
    "placeid" -> placeID.place_id
  )

}
