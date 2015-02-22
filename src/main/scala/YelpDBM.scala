import DB._
import YelpAPI.Business

//CREATE TABLE IF NOT EXISTS `yelp_businesses` (
//   `id` VARCHAR(128) NOT NULL,
//   `name` VARCHAR(128) NOT NULL,
//   `rating` DOUBLE NOT NULL,
//   `review_count` BIGINT NOT NULL,
//   `phone` CHAR(10),
//   `postal_code` CHAR(5) NOT NULL,
//   `address` VARCHAR(256) NOT NULL,
//   `latitude` DOUBLE NOT NULL,
//   `longitude` DOUBLE NOT NULL,
//
//   PRIMARY KEY (`id`)
//);
object YelpDBM {
  val yelp_businesses = Table("yelp_businesses")
  val columns = List(
    "id", // String
    "name", // String
    "rating", // Double
    "review_count", // BigInt
    "phone", // Option[String]
    "postal_code", // String
    "address", // String
    "latitude", // Double
    "longitude" // Double
  ).map(Column)

  def storeResult(business: Business) {
    val values = List(
      Some(business.id),
      Some(business.name),
      Some(business.rating),
      Some(business.review_count),
      business.phone,
      Some(business.location.postal_code.take(5)),
      Some(business.location.address.mkString(", ")),
      Some(business.location.coordinate.latitude),
      Some(business.location.coordinate.longitude)
    )

    val fields = (columns zip values).collect({ case(col, Some(value)) => (col, value) })

    DB.insertInto(yelp_businesses, fields)
  }
}
