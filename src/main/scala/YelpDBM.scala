import YelpAPI.Business
import com.clinkle.sql.{INSERT, Table}

object YelpDBM {
  def storeResult(business: Business) {
    import yelp_businesses._
    DBRunner.runInNewTransaction(implicit executor =>
      INSERT.INTO(yelp_businesses).SET(
        id := business.id,
        name := business.name,
        rating := business.rating,
        review_count := business.review_count.toLong,
        phone := business.phone,
        postal_code := business.location.postal_code.take(5),
        address := business.location.address.mkString(", "),
        latitude := business.location.coordinate.latitude,
        longitude := business.location.coordinate.longitude
      ).exec
    )
  }

  object yelp_businesses extends Table {
    val id: Column[String] = VARCHAR(128).PRIMARY_KEY
    val name: Column[String] = VARCHAR(128)
    val rating: Column[Double] = DOUBLE
    val review_count: Column[Long] = BIGINT
    val phone: Column[Option[String]] = CHAR(10).NULL
    val postal_code: Column[String] = CHAR(5)
    val address: Column[String] = VARCHAR(256)
    val latitude: Column[Double] = DOUBLE
    val longitude: Column[Double] = DOUBLE
  }
}
