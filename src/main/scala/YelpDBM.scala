import DB._
import YelpAPI.Business

//CREATE TABLE IF NOT EXISTS `yelp` (
//   `id` VARCHAR(128) NOT NULL,
//   `name` VARCHAR(128) NOT NULL,
//   `rating` DOUBLE NOT NULL,
//   `review_count` BIGINT NOT NULL,
//   `phone` VARCHAR(128),
//   `postal_code` VARCHAR(128) NOT NULL,
//   `address` VARCHAR(256) NOT NULL,
//   `latitude` DOUBLE NOT NULL,
//   `longitude` DOUBLE NOT NULL,
//
//   PRIMARY KEY (`id`)
//);
object YelpDBM {
  val table = Table("yelp")
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
      Some(business.location.postal_code),
      Some(business.location.address.mkString(", ")),
      Some(business.location.coordinate.latitude),
      Some(business.location.coordinate.longitude)
    )

    val fields = (columns zip values).collect({ case(col, Some(value)) => (col, value) })

    DB.insertInto(table, fields)
  }
}
