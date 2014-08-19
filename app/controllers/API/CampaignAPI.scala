package controllers.API

import controllers.API.UserAPI._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Controller, Action}

/**
 * Created by endamccormack on 18/08/2014.
 */

//Table definition
//`id` INT NOT NULL AUTO_INCREMENT,
//`Name` VARCHAR(100) NULL,
//`Description` TEXT NULL,
//`Account_id` INT NOT NULL,

object CampaignAPI extends Controller{

  def getAll = Action { implicit request =>
    val account_id: Option[String] = request.getQueryString("Account_id")

    account_id match{
      case Some(number) =>
        val campaigns = models.Campaign.findAllForAccount(number.toLong)
        Ok(Json.toJson(campaigns))
      case None =>
        val campaigns = models.Campaign.findAll()
        Ok(Json.toJson(campaigns))
    }

  }

  def getSingle(id:Long) = Action{
    val campaign = models.Campaign.findSingle(id)

    Ok(Json.toJson(campaign))
  }

  def deleteSingle(id:Long) = Action{

    val rowsUpdated = models.Campaign.deleteSingle(id)

    rowsUpdated match {
      case 1 =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"))
        Ok(Json.toJson(result))
      case 0 =>
        val result = Map("success" -> toJson(false), "msg" -> toJson("Zero rows affected means that your delete failed, fix it Enda"))
        Ok(Json.toJson(result))
    }
  }

  def insert(Name:String,
             Description:String,
             Account_id:Long) = Action{

    val newCampaign = models.Campaign(anorm.NotAssigned, Some(Name), Some(Description), Account_id )
    val id = models.Campaign.insert(newCampaign)

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
