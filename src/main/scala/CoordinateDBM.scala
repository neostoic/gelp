import GooglePlacesDBM.google_places
import YelpBusinessesDBM.yelp_businesses
import com.clinkle.sql.{INSERT, Table}

object CoordinateDBM {
  def recordGooglePlaceMatch(id: String, lat: Double, lng: Double, rad: Long) {
    import google_place_matches._
    DBRunner.runInNewTransaction(implicit executor =>
      INSERT.INTO(google_place_matches).SET(
        place_id := id,
        latitude := lat,
        longitude := lng,
        radius := rad
      ).exec
    )
  }

  def recordYelpMatch(yelpID: String, lat: Double, lng: Double, rad: Long) {
    import yelp_business_matches._
    DBRunner.runInNewTransaction(implicit executor =>
      INSERT.INTO(yelp_business_matches).SET(
        id := yelpID,
        latitude := lat,
        longitude := lng,
        radius := rad
      ).exec
    )
  }

  object google_place_matches extends Table {
    val place_id: Column[String] = VARCHAR(128)
    val latitude: Column[Double] = DOUBLE
    val longitude: Column[Double] = DOUBLE
    val radius: Column[Long] = BIGINT

    UNIQUE_KEY(place_id, latitude, longitude, radius)
    FOREIGN_KEY(place_id).REFERENCES(google_places.place_id)
  }

  object yelp_business_matches extends Table {
    val id: Column[String] = VARCHAR(128)
    val latitude: Column[Double] = DOUBLE
    val longitude: Column[Double] = DOUBLE
    val radius: Column[Long] = BIGINT

    UNIQUE_KEY(id, latitude, longitude, radius)
    FOREIGN_KEY(id).REFERENCES(yelp_businesses.id)
  }
}
