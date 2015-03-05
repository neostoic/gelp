object PlacesMatcher {
  def main(args: Array[String]) {
    val googlePlaces = GooglePlacesDBM.getBusinesses // 247 results
    val yelpPlaces = YelpBusinessesDBM.getBusinesses // 288 results

    val matchedByPhone = matchByPhone(googlePlaces, yelpPlaces)
    println(s"Found ${matchedByPhone.size} matches...\n")

    val matchedByName = matchByName(googlePlaces, yelpPlaces)
    println(s"Found ${matchedByName.size} matches...\n")

    val allMatches = (matchedByName ++ matchedByPhone).distinct
    println(s"Combined, there are now ${allMatches.size} matches...\n")

    val unmatchedGooglePlaces = googlePlaces diff allMatches.map(_._1)
    val unmatchedYelpPlaces = yelpPlaces diff allMatches.map(_._2)
    // Note: Suzu was the only false negative.
    println(s"There are ${unmatchedGooglePlaces.size} unmatched Google Places and ${unmatchedYelpPlaces.size} unmatched Yelp Places.")
  }

  private def matchByPhone(googlePlaces: List[GooglePlacesAPI.Business], yelpPlaces: List[YelpAPI.Business]) = {
    println("Matching businesses by phone...")
    val googlePlacesByPhone = googlePlaces.flatMap(gP => gP.formatted_phone_number.map(_.replaceAll("[^\\d]", "")).map((_, gP)))
    val yelpPlacesByPhone = yelpPlaces.flatMap(yP => yP.phone.map((_, yP)))

    for (
      (phone, googlePlace) <- googlePlacesByPhone;
      yelpPlace <- yelpPlacesByPhone.find(_._1 == phone).map(_._2)
    ) yield (googlePlace, yelpPlace)
  }

  private def matchByName(googlePlaces: List[GooglePlacesAPI.Business], yelpPlaces: List[YelpAPI.Business]) = {
    println("Matching businesses by name...")
    val googlePlacesByName = googlePlaces.map(gP => (gP.name, gP))
    val yelpPlacesByName = yelpPlaces.map(yP => (yP.name, yP))

    // TODO: need to protect against false positive matches, especially for businesses with short names.
    val googleNameMatches = googlePlacesByName.flatMap({ case(name, gP) =>
      yelpPlacesByName.find(_._1.contains(name)).map(yPMatch => (gP, yPMatch._2))
    })
    val yelpNameMatches = yelpPlacesByName.flatMap({ case(name, yP) =>
      googlePlacesByName.find(_._1.contains(name)).map(gPMatch => (gPMatch._2, yP))
    })

    (googleNameMatches ++ yelpNameMatches).distinct
  }
}
