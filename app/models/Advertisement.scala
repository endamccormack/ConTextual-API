package models

import anorm.{~, Pk}
import anorm.SqlParser._
import play.api.Logger
import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.libs.json._


/**
 * Created by endamccormack.
 */
//Table definition
//`id` INT NOT NULL,
//`Context` VARCHAR(45) NOT NULL,
//`Title` VARCHAR(45) NULL,
//`Content` TEXT NULL,
//`Campaign_id` INT NOT NULL,

case class Advertisement (
   id:          Pk[Long],
   Context:     String,
   Title:       Option[String],
   Content:     Option[String],
   Campaign_id: Long
)

object Advertisement{
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("Context") ~
    get[Option[String]]("Title") ~
    get[Option[String]]("Content") ~
    get[Long]("Campaign_id") map {
    case id~context~title~content~campaign_id =>
      Advertisement(
        id,
        context,
        title,
        content,
        campaign_id
      )
    }
  }

  def findSingle(id:Long):Advertisement = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Advertisement where id = '" + id + "'").as(Advertisement.simple *)
    }.head
  }

  def deleteSingle(id:Long):Int = {
    DB.withConnection{ implicit connection =>
      SQL("delete from Advertisement where id = '" + id + "'").executeUpdate()
    }
  }

  def findAll():Seq[Advertisement] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Advertisement").as(Advertisement.simple *)
    }
  }

  def findAllForCampaign(campaign_id:Long):Seq[Advertisement] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Advertisement where Campaign_id ='" + campaign_id + "'").as(Advertisement.simple *)
    }
  }

  def insert(advertisement: Advertisement):Option[Long] = {
    val id:Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Advertisement(Context, Title, Content, Campaign_id) " +
        "values({context},{title},{content},{campaign_id})").on(
          "context"       -> advertisement.Context,
          "title"         -> advertisement.Title,
          "content"       -> advertisement.Content,
          "campaign_id"   -> advertisement.Campaign_id
        ).executeInsert()
    }
    Logger.info("Insert into Advertisement table with id of " + id.toString())
    id
  }

  implicit object TransactionFormat extends Format[Advertisement] {
//    id:          Pk[Long],
//    Context:     String,
//    Title:       Option[String],
//    Content:     Option[String],
//    Campaign_id: Long
    // convert from Transaction object to JSON (serializing to JSON)
    def writes(advertisement: Advertisement): JsValue = {
      val accountSeq = Seq(
        "id"            -> JsNumber(advertisement.id.get),
        "Context"       -> JsString(advertisement.Context),
        "Title"         -> JsString(advertisement.Title.getOrElse("Unknown")),
        "Content"       -> JsString(advertisement.Content.getOrElse("Unknown")),
        "Campaign_id"   -> JsNumber(advertisement.Campaign_id)
      )
      JsObject(accountSeq)
    }

    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[Advertisement] = {
      val id = (json \ "id").as[Long]
      val Context = (json \ "Context").as[String]
      val Title = (json \ "Title").as[String]
      val Content = (json \ "Content").as[String]
      val Campaign_id = (json \ "Campaign_id").as[Long]

      JsSuccess(Advertisement(anorm.Id(id), Content, Some(Title), Some(Content), Campaign_id))
    }
  }

}