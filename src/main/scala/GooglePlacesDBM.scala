import GooglePlacesAPI.Business
import com.clinkle.sql.{INSERT, Table}

object GooglePlacesDBM {
  def storeResult(business: Business) {
    import GooglePlacesDBM.google_places._
    DBRunner.runInNewTransaction(implicit executor =>
      INSERT.INTO(google_places).SET(
        place_id := business.place_id,
        name := business.name,
        rating := business.rating,
        user_ratings_total := business.user_ratings_total.map(_.toLong),
        price_level := business.price_level.map(_.toLong),
        formatted_address := business.formatted_address,
        website := business.website,
        formatted_phone_number := business.formatted_phone_number,
        latitude := business.geometry.location.lat,
        longitude := business.geometry.location.lng
      ).exec
    )
  }

  object google_places extends Table {
    val place_id: Column[String] = VARCHAR(128).PRIMARY_KEY
    val name: Column[String] = VARCHAR(128)
    val rating: Column[Option[Double]] = DOUBLE.NULL
    val user_ratings_total: Column[Option[Long]] = BIGINT.NULL
    val price_level: Column[Option[Long]] = BIGINT.NULL
    val formatted_address: Column[String] = VARCHAR(128)
    val website: Column[Option[String]] = VARCHAR(256).NULL
    val formatted_phone_number: Column[Option[String]] = VARCHAR(128).NULL
    val latitude: Column[Double] = DOUBLE
    val longitude: Column[Double] = DOUBLE
  }
}
