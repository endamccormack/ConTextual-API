package models

import anorm.{~, Pk}
import anorm.SqlParser._
import play.api.Logger
import play.api.db.DB
import anorm._
import play.api.Play.current


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

  def findAll():Seq[Advertisement] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Advertisement").as(Advertisement.simple *)
    }
  }

  def insert(advertisement: Advertisement): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Advertisement(Context, Title, Content, Campaign_id) " +
        "values(,{context},{title},{content},{campaign_id})").on(
          "context"       -> advertisement.Context,
          "title"         -> advertisement.Title,
          "content"       -> advertisement.Content,
          "campaign_id"   -> advertisement.Campaign_id
        ).executeInsert()
    }
    Logger.info("Insert into Advertisement table with id of " + id.toString())
    id
  }
}