import DB._
import GooglePlacesAPI.Business

//CREATE TABLE IF NOT EXISTS `google_places` (
//   `place_id` VARCHAR(128) NOT NULL,
//   `name` VARCHAR(128) NOT NULL,
//   `rating` DOUBLE,
//   `user_ratings_total` BIGINT,
//   `price_level` BIGINT,
//   `formatted_address` VARCHAR(256) NOT NULL,
//   `website` VARCHAR(256),
//   `formatted_phone_number` VARCHAR(128),
//   `latitude` DOUBLE NOT NULL,
//   `longitude` DOUBLE NOT NULL,
//
//   PRIMARY KEY (`place_id`)
//);
object GooglePlacesDBM {
  val google_places = Table("google_places")
  val columns = List(
    "place_id", // String
    "name", // String
    "rating", // Option[Double]
    "user_ratings_total", // Option[BigInt]
    "price_level", // Option[BigInt]
    "formatted_address", // String
    "website", // Option[String]
    "formatted_phone_number", // Option[String]
    "latitude", // Double
    "longitude" // Double
  ).map(Column)

  def storeResult(business: Business) {
    val values = List(
      Some(business.place_id),
      Some(business.name),
      business.rating,
      business.user_ratings_total,
      business.price_level,
      Some(business.formatted_address),
      business.website,
      business.formatted_phone_number,
      Some(business.geometry.location.lat),
      Some(business.geometry.location.lng)
    )

    val fields = (columns zip values).collect({ case(col, Some(value)) => (col, value) })

    DB.insertInto(google_places, fields)
  }
}
