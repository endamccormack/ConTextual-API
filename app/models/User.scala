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
//`EmailAddress` VARCHAR(100) NOT NULL,
//`FirstName` VARCHAR(128) NULL,
//`LastName` VARCHAR(128) NULL,
//`JobTitle` VARCHAR(128) NULL,
//`Role` VARCHAR(45) NULL COMMENT '	',
//`Account_id` INT NOT NULL,

case class User (
  EmailAddress: Pk[String],
  FirstName:Option[String],
  LastName:Option[String],
  JobTitle:Option[String],
  Role:Option[String],
  Account_id:Long,
  password:String
)

object User{
  val simple = {
      get[Pk[String]]("EmailAddress") ~
      get[Option[String]]("FirstName") ~
      get[Option[String]]("LastName") ~
      get[Option[String]]("JobTitle") ~
      get[Option[String]]("Role") ~
      get[Long]("Account_id") ~
      get[String]("password") map {
          case emailadddress~firstname~lastname~jobtitle~role~account_id~password =>
            User(
              emailadddress,
              firstname,
              lastname,
              jobtitle,
              role,
              account_id,
              password
            )
        }
  }

  def findSingle(emailAddress:String):User = {
    DB.withConnection{ implicit connection =>
      SQL("select * from User where EmailAddress = '" + emailAddress + "'").as(User.simple *)
    }.head
  }

  def deleteSingle(emailAddress:String): Int = {
    DB.withConnection{ implicit connection =>
      val nRowsDeleted = SQL("delete from User where EmailAddress = {email}").on('email -> emailAddress).executeUpdate()
      nRowsDeleted
    }
  }

  def findAll():Seq[User] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from User").as(User.simple *)
    }
  }

  def insert(user: User): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into User(EmailAddress, FirstName, LastName, JobTitle, Role, Account_id, password) " +
          "values({email},{fname},{lname},{jobTitle},{role},{account_id}, {password})").on(
        "email"       -> user.EmailAddress,
        "fname"       -> user.FirstName,
        "lname"       -> user.LastName,
        "jobTitle"    -> user.JobTitle,
        "role"        -> user.Role,
        "account_id"  -> user.Account_id,
        "password"    -> user.password
      ).executeInsert()
    }
    Logger.info("Insert into User table with id of " + id.toString())
    id
  }

  implicit object TransactionFormat extends Format[User] {

    // convert from Transaction object to JSON (serializing to JSON)
    def writes(user: User): JsValue = {
      val accountSeq = Seq(
        "EmailAddress"    -> JsString(user.EmailAddress.get),
        "FirstName"       -> JsString(user.FirstName.getOrElse("Unknown")),
        "LastName"        -> JsString(user.LastName.getOrElse("Unknown")),
        "JobTitle"        -> JsString(user.JobTitle.getOrElse("Unknown")),
        "Role"            -> JsString(user.Role.getOrElse("Unknown")),
        "Account_id"      -> JsNumber(user.Account_id),
        "password"        -> JsString(user.password)
      )
      JsObject(accountSeq)
    }

    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[User] = {
      val EmailAddress = (json \ "EmailAddress").as[String]
      val FirstName = (json \ "FirstName").as[String]
      val LastName = (json \ "LastName").as[String]
      val JobTitle = (json \ "JobTitle").as[String]
      val Role = (json \ "Role").as[String]
      val Account_id = (json \ "Account_id").as[Long]
      val password = (json \ "password").as[String]

      JsSuccess(User(anorm.Id(EmailAddress), Some(FirstName), Some(LastName), Some(JobTitle), Some(Role), Account_id, password ))
    }
  }
}