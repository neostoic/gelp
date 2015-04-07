import external.{GooglePlacesAPI, YelpAPI}
import nominals.{Coordinate, Latitude, Longitude}
import utilities.CirclePacker

import scala.util.Random._

object Gelp {
  def main(args: Array[String]) {
    // The approximate coordinate bounds for zip code 94115.
    val vertices = List(
        (37.79255, -122.44780), // NW
        (37.79489, -122.42992), // NE
        (37.77922, -122.42679), // SE
        (37.77691, -122.44492)  // SW
      ).map(coord => Coordinate(Latitude(coord._1), Longitude(coord._2)))

    val meshRadius = 75
    val coordinatesToSearch = CirclePacker.generateSearchCoordinates(vertices, meshRadius)
    val numSearches = coordinatesToSearch.size

    try {
      shuffle(coordinatesToSearch).zipWithIndex.foreach({ case(coord, i) =>
        println(s"\nProgress: ${(i.toFloat / numSearches) * 100}%, Remaining: ${numSearches - i}\n")
        search(coord, meshRadius)
      })
    } finally { sys.exit() }
  }

  def search(coord: Coordinate, meshRadius: Int) {
    println(s"Running search at coordinate: ${coord.toSearchString}")
    YelpAPI.runYelpSearch(coord, radius = meshRadius)
    GooglePlacesAPI.runGoogleSearch(coord, radius = meshRadius)
    Thread.sleep(nextInt(22000) + 2000)
  }
}
