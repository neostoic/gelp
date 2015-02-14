class CirclePackerSpec extends UnitSpec {
  def coord(lat: Double, long: Double) = Coordinate(Latitude(lat), Longitude(long))

  describe("pointInPolygon") {
    it("should return true if a point is inside a rectangle") {
      val point = coord(1, 1)
      val polygon = List(coord(0,0), coord(0,4), coord(2,4), coord(2,0))
      assert(CirclePacker.pointInPolygon(point, polygon))
    }

    it("should return false if a point is outside a rectangle") {
      val point = coord(3, 1)
      val polygon = List(coord(0,0), coord(0,4), coord(2,0), coord(2,4))
      assert(!CirclePacker.pointInPolygon(point, polygon))
    }

    it("should return true if a point is inside an arbitrary quadrilateral") {
      val point = coord(3, 3)
      val polygon = List(coord(2,2), coord(2.1,4.1), coord(3.75,4.25), coord(3.5,3))
      assert(CirclePacker.pointInPolygon(point, polygon))
    }

    it("should work with negative numbers") {
      val point = coord(3, -3)
      val polygon = List(coord(2,-2), coord(2.1,-4.1), coord(3.75,-4.25), coord(3.5,-3))
      assert(CirclePacker.pointInPolygon(point, polygon))
    }
  }
}
