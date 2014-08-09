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
}