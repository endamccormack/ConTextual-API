
package models

import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._


/**
 * Created by endamccormack.
 */
//Table definition
//`id` INT NOT NULL AUTO_INCREMENT,
//`Name` VARCHAR(100) NULL,
//`Description` TEXT NULL,
//`Account_id` INT NOT NULL,

case class Campaign (
  id: Pk[Long],
  Name:Option[String],
  Description:Option[String],
  Account_id:Long
)

object Campaign{
  val simple = {
      get[Pk[Long]]("id") ~
      get[Option[String]]("Name") ~
      get[Option[String]]("Description") ~
      get[Long]("Account_id") map {
      case id~name~description~account_id =>
        Campaign(
          id,
          name,
          description,
          account_id
        )
    }
  }

  def findAll():Seq[Campaign] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Campaign").as(Campaign.simple *)
    }
  }

  def findAllForAccount(account_id:Long):Seq[Campaign] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Campaign where Account_id ='" + account_id+ "'").as(Campaign.simple *)
    }
  }

  def findSingle(id:Long):Campaign = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Campaign where id = '" + id + "'").as(Campaign.simple *)
    }.head
  }

  def deleteSingle(id:Long): Int = {
    DB.withConnection{ implicit connection =>
      SQL("delete from Campaign where id = '" + id + "'").executeUpdate()
    }
  }

  def insert(campaign: Campaign):Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Campaign(id, Name, Description, Account_id) " +
          "values({id},{name},{description},{account})").on(
          "id"          -> campaign.id,
          "name"        -> campaign.Name,
          "description" -> campaign.Description,
          "account"     -> campaign.Account_id
        ).executeInsert()
    }
    Logger.info("Insert into Campaign table with id of " + id.toString())
    id
  }

  implicit object TransactionFormat extends Format[Campaign] {

    // convert from Transaction object to JSON (serializing to JSON)
    def writes(campaign: Campaign): JsValue = {
      val accountSeq = Seq(
        "id"            -> JsNumber(campaign.id.get),
        "Name"          -> JsString(campaign.Name.getOrElse("Unknown")),
        "Description"   -> JsString(campaign.Description.getOrElse("Unknown")),
        "Account_id"    -> JsNumber(campaign.Account_id)
      )
      JsObject(accountSeq)
    }

    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[Campaign] = {
      val Id = (json \ "id").as[Long]
      val Name = (json \ "Name").as[String]
      val Description = (json \ "Description").as[String]
      val Account_id = (json \ "Account_id").as[Long]

      JsSuccess(Campaign(anorm.Id(Id), Some(Name), Some(Description), Account_id ))
    }
  }
}
