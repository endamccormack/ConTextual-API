package controllers.API

import controllers.API.UserAPI._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Controller, Action}

//Table definition
//`id` INT NOT NULL AUTO_INCREMENT,
//`CardNumber` VARCHAR(45) NOT NULL,
//`ExpiryDate` VARCHAR(45) NOT NULL,
//`CardHolderName` VARCHAR(255) NOT NULL,
//`CSV` VARCHAR(45) NULL,
//`CardType` VARCHAR(45) NOT NULL,
//`Account_id` INT NOT NULL,

/**
 * Created by endamccormack on 18/08/2014.
 */
object PaymentDetailAPI extends Controller {

  def getAll = Action {  implicit request =>

    val account_id: Option[String] = request.getQueryString("Account_id")

    account_id match{
      case Some(number) =>
        val paymentDetail = models.PaymentDetail.findAllForAccount(number.toLong)
        Ok(Json.toJson(paymentDetail))
      case None =>
        val paymentDetail = models.PaymentDetail.findAll()
        Ok(Json.toJson(paymentDetail))
    }
  }

  def getSingle(id:Long) = Action{
    val paymentDetail = models.PaymentDetail.findSingle(id)

    Ok(Json.toJson(paymentDetail))
  }

  def deleteSingle(id:Long) = Action{

    val rowsUpdated = models.PaymentDetail.deleteSingle(id)

    rowsUpdated match {
      case 1 =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"))
        Ok(Json.toJson(result))
      case 0 =>
        val result = Map("success" -> toJson(false), "msg" -> toJson("Zero rows affected means that your delete failed, fix it Enda"))
        Ok(Json.toJson(result))
    }
  }

  def insert(CardNumber:String,
             ExpiryDate:String,
             CardHolderName:String,
             CSV:String,
             CardType:String,
             Account_id:Long) = Action{

    val newPaymentDetail = models.PaymentDetail(anorm.NotAssigned, CardNumber, ExpiryDate, CardHolderName, Some(CSV), CardType, Account_id)
    val id = models.PaymentDetail.insert(newPaymentDetail)

    id match {
      case Some(autoIncrementId) =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))
        Ok(Json.toJson(result))
      case None =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(-1))
        Ok(Json.toJson(result))
    }
  }
}
