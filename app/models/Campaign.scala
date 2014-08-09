package models

/**
 * Created by endamccormack on 09/08/2014.
 */
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

  def insert(campaign: Campaign): Option[Long] = {
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
}
