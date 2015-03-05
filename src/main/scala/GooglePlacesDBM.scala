import GooglePlacesAPI.{PlaceCoordinate, Geometry, Business}
import com.clinkle.sql._
import com.clinkle.sql.Expr._

object GooglePlacesDBM {
  def storeResult(business: Business) {
    import google_places._
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

  def getBusinesses: List[Business] = {
    import google_places._
    DBRunner.runInNewTransaction(implicit executor =>
      SELECT_GOOGLE_PLACES_COLUMNS.exec.map({ case(pID, n, r, urt, pl, fa, w, fpn, lat, lng) =>
        Business(pID, n, r, urt.map(BigInt(_)), pl.map(BigInt(_)), fa, w, fpn, Geometry(PlaceCoordinate(lat, lng)))
      }).toList
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

    val SELECT_GOOGLE_PLACES_COLUMNS = SELECT(
      place_id,
      name,
      rating,
      user_ratings_total,
      price_level,
      formatted_address,
      website,
      formatted_phone_number,
      latitude,
      longitude
    ).FROM(this)
  }
}
