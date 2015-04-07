package utilities

import database.{MatchesDBM, GooglePlacesDBM, YelpBusinessesDBM}
import external.{GooglePlacesAPI, YelpAPI}
import nominals.Coordinate

object PlacesMatcher {
  case class Match(businesses: (GooglePlacesAPI.Business, YelpAPI.Business), score: Int)

  def main(args: Array[String]) {
    val googlePlaces = GooglePlacesDBM.getBusinesses
    val yelpPlaces = YelpBusinessesDBM.getBusinesses

    val allCombos = for(
      gPlace <- googlePlaces;
      yPlace <- yelpPlaces
    ) yield {
      val phoneScore = getPhoneScore(gPlace.phone, yPlace.phone)
      val nameScore = getNameScore(gPlace.name, yPlace.name)
      val addressScore = getAddressScore(gPlace.formatted_address, yPlace.address)
      val distanceScore = getDistanceScore(gPlace.coordinate, yPlace.coordinate)

      Match((gPlace, yPlace), phoneScore + nameScore + addressScore + distanceScore)
    }

    val bestMatches = allCombos.groupBy(_.businesses._1).map(_._2.maxBy(_.score)).filterNot(_.score == 0).toList
    println(s"Found a total of ${bestMatches.size} potential matches.")

    println("Strong matches:")
    bestMatches.filter(_.score >= 100).sortBy(-_.score)
      .foreach(m => {
        val percentMatch = Math.round(m.score / 2.7).toShort // 270 total points.
        println(s"${m.score} - $percentMatch%: ${m.businesses._1.name} -> ${m.businesses._2.name}")
        MatchesDBM.recordMatch(m.businesses._1.place_id, m.businesses._2.id, m.score, percentMatch)
      })

    println("Weak matches:")
    bestMatches.filter(_.score < 100).sortBy(-_.score)
      .foreach(m => println(s"${m.score} - ${Math.round(m.score/2.7)}%: ${m.businesses._1.name} -> ${m.businesses._2.name}"))
  }

  def getPhoneScore(gPhone: Option[String], yPhone: Option[String]): Int = if (gPhone == yPhone && gPhone.isDefined) 100 else 0

  def getNameScore(gName: String, yName: String): Int = {
    def normalize(string: String) = string.toLowerCase.replaceAll("[^a-z0-9]", "")
    val gNormalized = normalize(gName)
    val yNormalized = normalize(yName)

    if (gNormalized == yNormalized)
      70
    else if (gNormalized.contains(yNormalized) || yNormalized.contains(gNormalized))
      50
    else
      0
  }

  def getAddressScore(gAddress: String, yAddress: String): Int = {
    def streetAddress(string: String) = """^[0-9]+ [a-z]+\b""".r.findFirstIn(string.toLowerCase)
    val gNormalized = streetAddress(gAddress)
    val yNormalized = streetAddress(yAddress)

    if (gNormalized == yNormalized && gNormalized.isDefined)
      50
    else if (gNormalized.exists(yAddress.toLowerCase.contains) || yNormalized.exists(gAddress.toLowerCase.contains))
      40
    else
      0
  }

  def getDistanceScore(gCoordinate: Coordinate, yCoordinate: Coordinate): Int =
    gCoordinate.distanceTo(yCoordinate) match {
      case d if d < 5 => 50
      case d if d < 15 => 30
      case d if d < 25 => 10
      case _ => 0
    }
}
