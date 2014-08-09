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
//`CardNumber` VARCHAR(45) NOT NULL,
//`ExpiryDate` VARCHAR(45) NOT NULL,
//`CardHolderName` VARCHAR(255) NOT NULL,
//`CSV` VARCHAR(45) NULL,
//`CardType` VARCHAR(45) NOT NULL,
//`Account_id` INT NOT NULL,

case class PaymentDetail (
  id:             Pk[Long],
  CardNumber:     String,
  ExpiryDate:     String,
  CardHolderName: String,
  CSV:            Option[String],
  CardType:       String,
  Account_id:     Long
)

object PaymentDetail{
  val simple = {
    get[Pk[Long]]("id") ~
    get[String]("CardNumber") ~
    get[String]("ExpiryDate") ~
    get[String]("CardHolderName") ~
    get[Option[String]]("CSV") ~
    get[String]("CardType") ~
    get[Long]("Account_id") map {
    case id~cardnumber~expirydate~cardholdername~csv~cardtype~account_id =>
      PaymentDetail(
        id,
        cardnumber,
        expirydate,
        cardholdername,
        csv,
        cardtype,
        account_id
      )
    }
  }

  def findAll():Seq[PaymentDetail] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from PaymentDetail").as(PaymentDetail.simple *)
    }
  }

  def insert(paymentDetail: PaymentDetail): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      SQL("insert into PaymentDetail(CardNumber, ExpiryDate, CardHolderName, CSV, CardType, Account_id) " +
        "values({cardnumber},{expirydate},{name},{csv},{type},{account_id})").on(
          "cardnumber"       -> paymentDetail.CardNumber,
          "expirydate"       -> paymentDetail.ExpiryDate,
          "name"             -> paymentDetail.CardHolderName,
          "csv"              -> paymentDetail.CSV,
          "type"             -> paymentDetail.CardType,
          "account_id"       -> paymentDetail.Account_id
        ).executeInsert()
    }
    Logger.info("Insert into PaymentDetail table with id of " + id.toString())
    id
  }
}