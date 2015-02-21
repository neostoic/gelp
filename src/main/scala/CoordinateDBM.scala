import DB.{Column, Table}

//CREATE TABLE IF NOT EXISTS `google_place_matches` (
//   `place_id` VARCHAR(128) NOT NULL,
//   `latitude` DOUBLE NOT NULL,
//   `longitude` DOUBLE NOT NULL,
//   `radius` BIGINT NOT NULL,
//
//   UNIQUE KEY `entry` (`place_id`, `latitude`, `longitude`, `radius`),
//   FOREIGN KEY (`place_id`) REFERENCES `google_places`(`place_id`)
//);

//CREATE TABLE IF NOT EXISTS `yelp_business_matches` (
//   `id` VARCHAR(128) NOT NULL,
//   `latitude` DOUBLE NOT NULL,
//   `longitude` DOUBLE NOT NULL,
//   `radius` BIGINT NOT NULL,
//
//   UNIQUE KEY `entry` (`id`, `latitude`, `longitude`, `radius`),
//   FOREIGN KEY (`id`) REFERENCES `yelp`(`id`)
//);
object CoordinateDBM {
  val google_place_matches = Table("google_place_matches")
  val yelp_business_matches = Table("yelp_business_matches")

  def recordGooglePlaceMatch(id: String, lat: Double, lng: Double, radius: BigInt) {
    val fields = List(
      (Column("place_id"), id),
      (Column("latitude"), lat),
      (Column("longitude"), lng),
      (Column("radius"), radius)
    )

    DB.insertInto(google_place_matches, fields)
  }

  def recordYelpMatch(id: String, lat: Double, lng: Double, radius: BigInt) {
    val fields = List(
      (Column("id"), id),
      (Column("latitude"), lat),
      (Column("longitude"), lng),
      (Column("radius"), radius)
    )

    DB.insertInto(yelp_business_matches, fields)
  }
}
