object CirclePacker {

  def generateSearchCoordinates(vertices: List[Coordinate], meshRadius: Int): List[Coordinate] = {
    // Step 1: Create an encompassing rectangle.
    val (minCoord, maxCoord) = getEncompassingRectangle(vertices)

    // Step 2: Mesh the encompassing rectangle.
    val encompassingRectangleMesh = meshRectangle(minCoord, maxCoord, meshRadius)

    // Step 3: Mask the grid to the desired area.
    maskMesh(encompassingRectangleMesh, mask = vertices)
  }

  def getEncompassingRectangle(vertices: List[Coordinate]): (Coordinate, Coordinate) = {
    val lats = vertices.map(_.lat)
    val longs = vertices.map(_.long)

    (Coordinate(lats.minBy(_.value), longs.minBy(_.value)), Coordinate(lats.maxBy(_.value), longs.maxBy(_.value)))
  }
  
  def meshRectangle(minCoord: Coordinate, maxCoord: Coordinate, meshRadius: Int): List[Coordinate] = {
    // For now, start with a simple square layout wherein groups of four circles touch in the middle.
    val meshSize = Math.sqrt(2) * meshRadius

    val latCoords = Stream.iterate(minCoord.lat)(_ + meshSize).takeWhile(_ < maxCoord.lat).toList
    val longCoords = Stream.iterate(minCoord.long)(_ + meshSize).takeWhile(_ < maxCoord.long).toList

    for { lat <- latCoords; long <- longCoords } yield Coordinate(lat, long)
  }

  def maskMesh(encompassingMesh: List[Coordinate], mask: List[Coordinate]): List[Coordinate] =
    encompassingMesh.filter(pointInPolygon(_, mask))

  def pointInPolygon(point: Coordinate, vertices: List[Coordinate]): Boolean = {
    (vertices.last :: vertices).sliding(2).foldLeft(false) { case (cond, List(i, j)) =>
      val insideLatBounds = (i.lat > point.lat) != (j.lat > point.lat)
      val longValueOnBoundary = i.long.value + (j.long.value - i.long.value) * (point.lat.value - i.lat.value) / (j.lat.value - i.lat.value)
      val insideLongBounds = point.long.value < longValueOnBoundary

      if (insideLatBounds && insideLongBounds) !cond else cond
    }
  }
}
