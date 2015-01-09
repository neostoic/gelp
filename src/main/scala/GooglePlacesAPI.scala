import dispatch._
import Defaults._
import org.json4s.native.JsonMethods._

object GooglePlacesAPI {

  private val BROWSER_API_KEY = ""
  private val SERVER_API_KEY = ""
  private val NEARBY_SEARCH_URL = url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
  private val PLACE_DETAILS_URL = url("https://maps.googleapis.com/maps/api/place/details/json")

  def runGoogleSearch() {
    println("Let's get this Google party started!")

    val request = searchURL
    val response = Http(request OK as.String)

    val jsonResp = parse(s"""${ response() }""")

    // JSON Documentation: https://github.com/json4s/json4s
    println(s"Status is: ${ (jsonResp \ "status").values }")
    println(pretty(render(jsonResp)))
  }

  // Documentation: https://developers.google.com/places/documentation/search#PlaceSearchRequests
  def searchURL = NEARBY_SEARCH_URL <<? searchQueryParams
  def searchQueryParams = Map(
    "key" -> SERVER_API_KEY,
    "location" -> "37.776472,-122.437833", // The Mill
    "radius" -> "50",
    "types" -> "bakery|bar|cafe|food|restaurant"
  )

  // Documentation: https://developers.google.com/places/documentation/details
  def detailsURL(placeID: String) = PLACE_DETAILS_URL <<? detailsQueryParams(placeID)
  def detailsQueryParams(placeID: String) = Map(
    "key" -> SERVER_API_KEY,
    "placeid" -> placeID
  )

}
