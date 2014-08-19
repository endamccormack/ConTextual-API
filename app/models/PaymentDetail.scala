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

  def findAllForAccount(account_id:Long):Seq[PaymentDetail] = {
    DB.withConnection{ implicit connection =>
      SQL("select * from PaymentDetail where account_id =" + account_id).as(PaymentDetail.simple *)
    }
  }

  def findSingle(id:Long):PaymentDetail = {
    DB.withConnection{ implicit connection =>
      SQL("select * from PaymentDetail where id = '" + id + "'").as(PaymentDetail.simple *)
    }.head
  }

  def deleteSingle(id:Long):Int = {
    DB.withConnection{ implicit connection =>
      SQL("delete from PaymentDetail where id = '" + id + "'").executeUpdate()
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

  implicit object TransactionFormat extends Format[PaymentDetail] {

//    get[Pk[Long]]("id") ~
//      get[String]("CardNumber") ~
//      get[String]("ExpiryDate") ~
//      get[String]("CardHolderName") ~
//      get[Option[String]]("CSV") ~
//      get[String]("CardType") ~
//      get[Long]("Account_id")
    // convert from Transaction object to JSON (serializing to JSON)
    def writes(paymentDetail: PaymentDetail): JsValue = {
      val accountSeq = Seq(
        "id"              -> JsNumber(paymentDetail.id.get),
        "CardNumber"      -> JsString(paymentDetail.CardNumber),
        "ExpiryDate"      -> JsString(paymentDetail.ExpiryDate),
        "CardHolderName"  -> JsString(paymentDetail.CardHolderName),
        "CSV"             -> JsString(paymentDetail.CSV.getOrElse("")),
        "CardType"        -> JsString(paymentDetail.CardType),
        "Account_id"      -> JsNumber(paymentDetail.Account_id)
      )
      JsObject(accountSeq)
    }

    // convert from a JSON string to a Transaction object (de-serializing from JSON)
    def reads(json: JsValue): JsResult[PaymentDetail] = {
      val id = (json \ "id").as[Long]
      val CardNumber = (json \ "CardNumber").as[String]
      val ExpiryDate = (json \ "ExpiryDate").as[String]
      val CardHolderName = (json \ "CardHolderName").as[String]
      val CSV = (json \ "CSV").as[String]
      val CardType = (json \ "CardType").as[String]
      val Account_id = (json \ "Account_id").as[Long]

      JsSuccess(PaymentDetail(anorm.Id(id), CardNumber, ExpiryDate, CardHolderName, Some(CSV), CardType, Account_id ))
    }
  }
}