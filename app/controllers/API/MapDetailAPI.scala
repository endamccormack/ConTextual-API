package controllers.API

import controllers.API.PaymentDetailAPI._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

/**
 * Created by endamccormack on 20/08/2014.
 */


object MapDetailAPI extends Controller {

  def getAll = Action {  implicit request =>

    val account_id: Option[String] = request.getQueryString("Account_id")

    account_id match{
      case Some(number) =>
        val paymentDetail = models.MapDetail.findAllForAccount(number.toLong)
        Ok(Json.toJson(paymentDetail))
      case None =>
        val paymentDetail = models.MapDetail.findAll()
        Ok(Json.toJson(paymentDetail))
    }
  }
}
