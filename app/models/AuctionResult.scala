package models

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Logger
import play.api.db.DB
import myUtils.AnormExtension._
import play.api.Play.current


/**
 * Created by endamccormack.
 */
//Table definition
//`id` INT NOT NULL AUTO_INCREMENT,
//`StartTime` DATETIME NOT NULL,
//`EndTime` DATETIME NOT NULL,
//`Context` VARCHAR(45) NULL,
//`Title` VARCHAR(45) NULL,
//`Content` TEXT NULL,
//`BidId` INT NULL,
//`StreetAddress` VARCHAR(100) NULL,
//`City` VARCHAR(100) NULL,
//`Country` VARCHAR(100) NULL,
//`PostCode` VARCHAR(45) NULL,


case class AuctionResult (
  id:             Pk[Long],
  StartTime:      Option[DateTime],
  EndTime:        Option[DateTime],
  Context:        Option[String],
  Title:          Option[String],
  Content:        Option[String],
  BidId:          Long,
  StreetAddress:  Option[String],
  City:           Option[String],
  Country:        Option[String],
  PostCode:       Option[String]
)

object AuctionResult {
//  val simple = {
//    get[Pk[Long]]("id") ~
//    get[Option[DateTime]]("StartTime") ~
//    get[Option[DateTime]]("EndTime") ~
//    get[Option[String]]("Context") ~
//    get[Option[String]]("Title") ~
//    get[Option[String]]("Content") ~
//    get[Long]("Bid_id") ~
//    get[Option[String]]("StreetAddress") ~
//    get[Option[String]]("City") ~
//    get[Option[String]]("PostCode") ~
//    get[Option[String]]("Country") map {
//    case id ~ starttime ~ endtime ~ context ~ title ~ content ~ bid_id ~ streetaddress ~ city ~ postcode ~ country =>
//      AuctionResult
//      (
//        id,
//        starttime,
//        endtime,
//        context,
//        title,
//        content,
//        bid_id,
//        streetaddress,
//        city,
//        postcode,
//        country
//      )
//    }
//  }

  val simple = {
    get[Pk[Long]]("id") ~
    get[Option[DateTime]]("StartTime") ~
    get[Option[DateTime]]("EndTime") ~
    get[Option[String]]("Context") ~
    get[Option[String]]("Title") ~
    get[Option[String]]("Content") ~
    get[Long]("BidId") ~
    get[Option[String]]("StreetAddress") ~
    get[Option[String]]("City") ~
    get[Option[String]]("Country") ~
    get[Option[String]]("PostCode") map {
    case id~starttime~endtime~context~title~content~bidid~streetaddress~city~country~postcode =>
      AuctionResult(
        id,
        starttime,
        endtime,
        context,
        title,
        content,
        bidid,
        streetaddress,
        city,
        country,
        postcode
      )
    }
  }

  def findAll():Seq[AuctionResult] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from AuctionResult").as(AuctionResult.simple *)
    }
  }

  def insert(auctionResult: AuctionResult): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Bid(id, StartTime, EndTime, Context, Title, Content, Bid_id, StreetAddress, City, PostCode, Country) " +
        "values({starttime},{endtime},{context},{title},{content},{bid_id},{street},{city},{postcode},{country})").on(
          "startime"    -> auctionResult.StartTime,
          "endtime"     -> auctionResult.EndTime,
          "context"     -> auctionResult.Context,
          "title"       -> auctionResult.Title,
          "content"     -> auctionResult.Content,
          "bid_id"      -> auctionResult.BidId,
          "street"      -> auctionResult.StreetAddress,
          "city"        -> auctionResult.City,
          "postcode"    -> auctionResult.PostCode,
          "country"     -> auctionResult.Country

        ).executeInsert()
    }
    Logger.info("Insert into AuctionResult table with id of " + id.toString())
    id
  }
}
