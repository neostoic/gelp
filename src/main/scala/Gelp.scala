import dispatch._
import Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._


object Gelp {
  private val BROWSER_API_KEY = ""
  private val SERVER_API_KEY = ""
  private val NEARBY_SEARCH_URL = url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
  private val PLACE_DETAILS_URL = url("https://maps.googleapis.com/maps/api/place/details/json")

  def main(args: Array[String]) {
    println("Let's get this party started!")
    // Documentation: https://developers.google.com/places/documentation/search#PlaceSearchRequests

    val request = searchURL
    val response = Http(request OK as.String)

    val jsonResp = parse(s"""${ response() }""")

    // JSON Documentation: https://github.com/json4s/json4s
    println(s"Status is: ${ (jsonResp \ "status").values }")
    println(pretty(render(jsonResp)))
  }

  def searchURL = NEARBY_SEARCH_URL <<? searchQueryParams
  def searchQueryParams = Map(
    "key" -> SERVER_API_KEY,
    "location" -> "37.776472,-122.437833", // The Mill
    "radius" -> "50",
    "types" -> "bakery|bar|cafe|food|restaurant"
    // types - Restricts the results to places matching at least one of the specified types. Types should be separated with a pipe symbol (type1|type2|etc). See: https://developers.google.com/places/documentation/supported_types
  )

  def detailsURL(placeID: String) = PLACE_DETAILS_URL <<? detailsQueryParams(placeID)
  def detailsQueryParams(placeID: String) = Map(
    "key" -> SERVER_API_KEY,
    "placeid" -> placeID
  )
}
