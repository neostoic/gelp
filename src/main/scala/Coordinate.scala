case class Coordinate(lat: Latitude, long: Longitude) {
  def distanceTo(other: Coordinate): Double =
    Math.sqrt(Math.pow(lat - other.lat, 2) + Math.pow(long - other.long, 2))

  def toSearchString = s"${lat.value},${long.value}"
}
object Coordinate {
  def apply(lat: Double, lng: Double): Coordinate = Coordinate(Latitude(lat), Longitude(lng))
}

case class Latitude(value: Double) {
  // 0.0001 of a degree is approximately 8.8m North/South in San Francisco (i.e. at 37.78 degrees latitude).
  def -(other: Latitude): Double = (value - other.value) * 88000
  def -(distance: Double): Latitude = Latitude(value - distance / 88000)
  def +(distance: Double): Latitude = Latitude(value + distance / 88000)

  def >(other: Latitude): Boolean = value > other.value
  def >=(other: Latitude): Boolean = value >= other.value
  def <(other: Latitude): Boolean = value < other.value
  def <=(other: Latitude): Boolean = value <= other.value
}

case class Longitude(value: Double) {
  // 0.0001 of a degree is approximately 11.1m East/West in San Francisco (i.e. at 37.78 degrees latitude).
  def -(other: Longitude): Double = (value - other.value) * 111000
  def -(distance: Double): Longitude = Longitude(value - distance / 111000)
  def +(distance: Double): Longitude = Longitude(value + distance / 111000)

  def >(other: Longitude): Boolean = value > other.value
  def >=(other: Longitude): Boolean = value >= other.value
  def <(other: Longitude): Boolean = value < other.value
  def <=(other: Longitude): Boolean = value <= other.value
}
