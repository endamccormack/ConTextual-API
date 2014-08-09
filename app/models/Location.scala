package models

import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.db.DB
import play.api.Play.current

/**
 * Created by endamccormack.
 */
//Table definition
//`id` INT NOT NULL AUTO_INCREMENT,
//`StreetAddress` VARCHAR(100) NULL,
//`City` VARCHAR(100) NULL,
//`Country` VARCHAR(100) NULL,
//`PostCode` VARCHAR(45) NULL,
//`xGeoLocation` VARCHAR(45) NULL,
//`yGeoLocation` VARCHAR(45) NULL,

case class Location (
  id:             Pk[Long],
  StreetAddress:  Option[String],
  City:           Option[String],
  Country:        Option[String],
  PostCode:       Option[String],
  xGeoLocation:   Option[String],
  yGeoLocation:   Option[String]
)

object Location{
  val simple = {
    get[Pk[Long]]("id") ~
    get[Option[String]]("StreetAddress") ~
    get[Option[String]]("City") ~
    get[Option[String]]("Country") ~
    get[Option[String]]("PostCode") ~
    get[Option[String]]("xGeoLocation") ~
    get[Option[String]]("yGeoLocation") map {
    case id~streetname~city~country~postcode~xgeolocation~ygeolocation =>
      Location(
        id,
        streetname,
        city,
        country,
        postcode,
        xgeolocation,
        ygeolocation
      )
    }
  }

  def findAll():Seq[Location] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Location").as(Location.simple *)
    }
  }

  def insert(location: Location): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Location(id, StreetAddress, City, Country, PostCode, xGeoLocation, yGeoLocation) " +
        "values({id},{street},{city},{country},{postcode},{xgeo},{ygeo})").on(
          "id"       -> location.id,
          "street"   -> location.StreetAddress,
          "city"     -> location.City,
          "country"  -> location.Country,
          "postcode" -> location.PostCode,
          "xgeo"     -> location.xGeoLocation,
          "ygeo"     -> location.yGeoLocation
        ).executeInsert()
    }
    Logger.info("Insert into Location table with id of " + id.toString())
    id
  }
}