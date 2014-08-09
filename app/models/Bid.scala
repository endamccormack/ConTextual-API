package models

import anorm.{~, Pk}
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.DB
import anorm._
import myUtils.AnormExtension._
import play.api.Play.current

/**
 * Created by endamccormack.
 */
//`id` INT NOT NULL AUTO_INCREMENT,
//`StartTime` DATETIME NULL,
//`EndTime` DATETIME NULL,
//`Advertisement_id` INT NOT NULL,
//`Location_id` INT NOT NULL,

case class Bid (
   id: Pk[Long],
   StartTime:Option[DateTime],
   EndTime:Option[DateTime],
   Advertisement_id:Long,
   Location_id:Long
 )

object Bid{
  val simple = {
    get[Pk[Long]]("id") ~
    get[Option[DateTime]]("StartTime") ~
    get[Option[DateTime]]("EndTime") ~
    get[Long]("Advertisement_id") ~
    get[Long]("Location_id") map {
    case id~starttime~endtime~advertisementid~locationid =>
      Bid(
        id,
        starttime,
        endtime,
        advertisementid,
        locationid
      )
    }
  }

  def findAll():Seq[Bid] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Bid").as(Bid.simple *)
    }
  }

  def insert(bid: Bid): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into User(id, StartTime, EndTime, Advertisement_id, Location_id) " +
        "values({theId},{starttime},{endtime},{advertid},{locationid})").on(
          "theId"         -> bid.id,
          "starttime"     -> bid.StartTime,
          "endtime"       -> bid.EndTime,
          "advertid"      -> bid.Advertisement_id,
          "locationid"    -> bid.Location_id
        ).executeInsert()
    }
    Logger.info("Insert into bid table with id of " + id.toString())
    id
  }
}

