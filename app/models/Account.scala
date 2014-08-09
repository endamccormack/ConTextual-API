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
case class Account (id: Pk[Long], CompanyName:String)

object Account{
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("CompanyName") map {
      case id~companyname => Account(id,companyname)
    }
  }

  def findAll():Seq[Account] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Account").as(Account.simple *)
    }
  }

  def insert(account: Account): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into Account(CompanyName) values({companyName})").on(
        "companyName" -> account.CompanyName
      ).executeInsert()
    }
    Logger.info("Insert into account table with id of " + id.toString())
    id
  }
}