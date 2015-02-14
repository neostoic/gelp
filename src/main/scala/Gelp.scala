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

    val coordinatesToSearch = CirclePacker.generateSearchCoordinates(vertices, meshRadius = 100)

    shuffle(coordinatesToSearch).take(5).foreach(coord => {
      println(s"Running search at coordinate: ${coord.toSearchString}")
      YelpAPI.runYelpSearch(coord, radius = 100)
      GooglePlacesAPI.runGoogleSearch(coord, radius = 100)
      Thread.sleep(nextInt(12000))
    })

    sys.exit()
  }
}
