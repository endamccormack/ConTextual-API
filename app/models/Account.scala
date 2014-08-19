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
case class Account (id: Pk[Long], CompanyName:String)

object Account{
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("CompanyName") map {
      case id~companyname => Account(id,companyname)
    }
  }

  def findSingle(id:Long):Account = {
    DB.withConnection{ implicit connection =>
      SQL("select * from Account where id=" + id.toString()).as(Account.simple *)
    }.head
  }

  def deleteSingle(id:Long):Int = {
    DB.withConnection{ implicit connection =>
      SQL("delete from Account where id = '" + id + "'").executeUpdate()
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

  implicit object TransactionFormat extends Format[Account] {

    // convert from Transaction object to JSON (serializing to JSON)
    def writes(account: Account): JsValue = {
      val accountSeq = Seq(
        "id" -> JsNumber(account.id.get),
        "CompanyName" -> JsString(account.CompanyName)
      )
      JsObject(accountSeq)
    }

    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[Account] = {
      val id = (json \ "id").as[Long]
      val companyName = (json \ "CompanyName").as[String]
      JsSuccess(Account( anorm.Id(id), companyName))
    }
  }
}