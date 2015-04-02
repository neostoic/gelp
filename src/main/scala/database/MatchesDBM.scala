package database

import com.clinkle.sql.{INSERT, Table}

object MatchesDBM {
  def recordMatch(googleID: String, yelpID: String, score: Int, matchPercent: Short) {
    DBRunner.runInNewTransaction(implicit executor =>
      INSERT.INTO(place_matches).SET(
        place_matches.google_id := googleID,
        place_matches.yelp_id := yelpID,
        place_matches.raw_score := score,
        place_matches.match_percent := matchPercent
      ).ON_DUPLICATE_KEY_UPDATE(
        place_matches.raw_score := score,
        place_matches.match_percent := matchPercent
      ).exec
    )
  }

  object place_matches extends Table {
    val google_id: Column[String] = VARCHAR(128)
    val yelp_id: Column[String] = VARCHAR(128)
    val raw_score: Column[Int] = INT
    val match_percent: Column[Short] = SMALLINT

    UNIQUE_KEY(google_id, yelp_id)
    FOREIGN_KEY(google_id).REFERENCES(GooglePlacesDBM.google_places.place_id)
    FOREIGN_KEY(yelp_id).REFERENCES(YelpBusinessesDBM.yelp_businesses.id)
  }
}
