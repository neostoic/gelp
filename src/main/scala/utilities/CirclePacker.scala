package utilities

import nominals.Coordinate

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
    val lngs = vertices.map(_.lng)

    (Coordinate(lats.minBy(_.value), lngs.minBy(_.value)), Coordinate(lats.maxBy(_.value), lngs.maxBy(_.value)))
  }
  
  def meshRectangle(minCoord: Coordinate, maxCoord: Coordinate, meshRadius: Int): List[Coordinate] = {
    // For now, start with a simple square layout wherein groups of four circles touch in the middle.
    val meshSize = Math.sqrt(2) * meshRadius

    val latCoords = Stream.iterate(minCoord.lat)(_ + meshSize).takeWhile(_ < maxCoord.lat).toList
    val lngCoords = Stream.iterate(minCoord.lng)(_ + meshSize).takeWhile(_ < maxCoord.lng).toList

    for { lat <- latCoords; lng <- lngCoords } yield Coordinate(lat, lng)
  }

  def maskMesh(encompassingMesh: List[Coordinate], mask: List[Coordinate]): List[Coordinate] =
    encompassingMesh.filter(pointInPolygon(_, mask))

  def pointInPolygon(point: Coordinate, vertices: List[Coordinate]): Boolean = {
    (vertices.last :: vertices).sliding(2).foldLeft(false) { case (cond, List(i, j)) =>
      val insideLatBounds = (i.lat > point.lat) != (j.lat > point.lat)
      val lngValueOnBoundary = i.lng.value + (j.lng.value - i.lng.value) * (point.lat.value - i.lat.value) / (j.lat.value - i.lat.value)
      val insideLngBounds = point.lng.value < lngValueOnBoundary

      if (insideLatBounds && insideLngBounds) !cond else cond
    }
  }
}
