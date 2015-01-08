import dispatch._
import Defaults._

object Gelp {
  private val BROWSER_API_KEY = ""
  private val SERVER_API_KEY = ""
  private val NEARBY_SEARCH_URL = url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")

  def main(args: Array[String]) {
    println("Let's get this party started!")
    // Documentation: https://developers.google.com/places/documentation/search#PlaceSearchRequests

    val request = searchURL
    val response = Http(request OK as.String)
    for (r <- response)
      println(r)
  }

  def searchURL = NEARBY_SEARCH_URL <<? queryParams

  def queryParams = Map(
    "key" -> SERVER_API_KEY,
    "location" -> "37.7833,122.4167",
    "radius" -> "5000"
    // types - Restricts the results to places matching at least one of the specified types. Types should be separated with a pipe symbol (type1|type2|etc). See: https://developers.google.com/places/documentation/supported_types
  )

}
