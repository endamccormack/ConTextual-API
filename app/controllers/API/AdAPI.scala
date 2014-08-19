package controllers.API

import controllers.API.UserAPI._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Controller, Action}

//Table definition
//`id` INT NOT NULL,
//`Context` VARCHAR(45) NOT NULL,
//`Title` VARCHAR(45) NULL,
//`Content` TEXT NULL,
//`Campaign_id` INT NOT NULL,
/**
 * Created by endamccormack on 18/08/2014.
 */
object AdAPI extends Controller{

  def getAll = Action {  implicit request =>

    val campaign_id: Option[String] = request.getQueryString("Campaign_id")

    campaign_id match{
      case Some(number) =>
        val advertisements = models.Advertisement.findAllForCampaign(number.toLong)
        Ok(Json.toJson(advertisements))
      case None =>
        val advertisements = models.Advertisement.findAll()
        Ok(Json.toJson(advertisements))
    }

  }

  def getSingle(id:Long) = Action{
    val advertisement = models.Advertisement.findSingle(id)

    Ok(Json.toJson(advertisement))
  }

  def deleteSingle(id:Long) = Action{

    val rowsUpdated = models.Advertisement.deleteSingle(id)

    rowsUpdated match {
      case 1 =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"))
        Ok(Json.toJson(result))
      case 0 =>
        val result = Map("success" -> toJson(false), "msg" -> toJson("Zero rows affected means that your delete failed, fix it Enda"))
        Ok(Json.toJson(result))
    }
  }

  def insert(Context:String,
             Title:String,
             Content:String,
             campaign_id:Long) = Action{

    val newAdvertisement = models.Advertisement(anorm.NotAssigned, Context, Some(Title), Some(Content), campaign_id )
    val id = models.Advertisement.insert(newAdvertisement)

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
