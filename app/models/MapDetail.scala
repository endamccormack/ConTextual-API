package models

import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.db.DB
import play.api.libs.json._
import play.api.Play.current

/**
 * Created by endamccormack on 20/08/2014.
 */

case class MapDetail (
           id:              Pk[Long],
           Title:           String,
           Description:     String,
           Lat:             String,
           Long:            String,
           Account_id:      Long,
           Campaign_id:     Long
           )

object MapDetail{
  val simple = {
      get[Pk[Long]]("id") ~
      get[String]("Title") ~
      get[String]("Detail") ~
      get[String]("Lat") ~
      get[String]("Long") ~
      get[Long]("Account_id") ~
      get[Long]("Campaign_id") map {
      case id~title~detail~lat~long~account_id~campaign_id =>
        MapDetail(
          id,
          title,
          detail,
          lat,
          long,
          account_id,
          campaign_id
        )
    }
  }

  def findAll():Seq[MapDetail] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from map").as(MapDetail.simple *)
    }
  }

  def findAllForAccount(account_id:Long):Seq[MapDetail] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from map where account_id =" + account_id).as(MapDetail.simple *)
    }
  }

  implicit object TransactionFormat extends Format[MapDetail] {

    def writes(mapDetail: MapDetail): JsValue = {
      val accountSeq = Seq(
        "id"                -> JsNumber(mapDetail.id.get),
        "Title"             -> JsString(mapDetail.Title),
        "Description"       -> JsString(mapDetail.Description),
        "Lat"               -> JsString(mapDetail.Lat),
        "Long"              -> JsString(mapDetail.Long),
        "Account_id"        -> JsNumber(mapDetail.Account_id),
        "Campaign_id"       -> JsNumber(mapDetail.Campaign_id)
      )
      JsObject(accountSeq)
    }
    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[MapDetail] = {
      val id = (json \ "id").as[Long]
      val Title = (json \ "Title").as[String]
      val Description = (json \ "Description").as[String]
      val Lat = (json \ "Lat").as[String]
      val Long = (json \ "Long").as[String]
      val Account_id = (json \ "Account_id").as[Long]
      val Campaign_id = (json \ "Campaign_id").as[Long]

      JsSuccess(MapDetail(anorm.Id(id), Title, Description, Lat, Long, Account_id, Campaign_id ))
    }

  }
}
