import dispatch._
import Defaults._
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._

import scala.util.Random._

object GooglePlacesAPI {

  private val BROWSER_API_KEY = ""
  private val SERVER_API_KEY = ""
  private val NEARBY_SEARCH_URL = url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
  private val PLACE_DETAILS_URL = url("https://maps.googleapis.com/maps/api/place/details/json")

  case class PlaceID(place_id: String)
  case class PlaceCoordinate(lat: Double, lng: Double)
  case class Geometry(location: PlaceCoordinate)
  case class Business(place_id: String, name: String, rating: Option[Double], user_ratings_total: Option[BigInt], price_level: Option[BigInt], formatted_address: String, website: Option[String], formatted_phone_number: Option[String], geometry: Geometry)  {
    def coordinate = Coordinate(geometry.location.lat, geometry.location.lng)
    def phone = formatted_phone_number.map(_.replaceAll("[^\\d]", ""))
    def toDisplayString =
      s"""
         |Name: $name
         |Rating: ${rating.getOrElse("N/A")}
         |Reviews: ${user_ratings_total.getOrElse("N/A")}
         |Phone: ${formatted_phone_number.getOrElse("N/A")}
         |Address: $formatted_address
       """.stripMargin
  }

  def runGoogleSearch(coord: Coordinate, radius: Int = 50) {
    println("\nLet's get this Google party started!")

    val response = send(searchRequest(coord, radius))

    implicit val formats = DefaultFormats
    val placeIDs = (response \ "results").extract[List[PlaceID]]
    val placeDetails = placeIDs.map(id => {
      Thread.sleep(nextInt(700))
      send(detailsRequest(id))
    })
    val businesses = placeDetails.map(placeDetail => (placeDetail \ "result").extract[Business])

    businesses.foreach(business => {
      println(business.toDisplayString)

      GooglePlacesDBM.storeResult(business)

      val coord = business.geometry.location
      CoordinateDBM.recordGooglePlaceMatch(business.place_id, coord.lat, coord.lng, radius)
    })
  }

  def send(request: Req) = {
    val rawResp = Http(request OK as.String)
    val jsonResp = parse(s"""${ rawResp() }""")

    println(s"\nStatus is: ${ (jsonResp \ "status").values }")
//    println(pretty(render(jsonResp)))

    jsonResp
  }

  // Documentation: https://developers.google.com/places/documentation/search#PlaceSearchRequests
  def searchRequest(coord: Coordinate, radius: Int) = NEARBY_SEARCH_URL <<? Map(
    "key" -> SERVER_API_KEY,
    "location" -> coord.toSearchString,
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
