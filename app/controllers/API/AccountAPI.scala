package controllers.API

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

/**
 * Created by endamccormack on 11/08/2014.
 */
object AccountAPI extends Controller{

  def getAll = Action {
    val accounts = models.Account.findAll()

    Ok(Json.toJson(accounts))
  }

  def getSingle(id:Long) = Action{
    val account = models.Account.findSingle(id)

    Ok(Json.toJson(account))

  }

  def deleteSingle(id:Long) = Action{

    val rowsUpdated = models.Account.deleteSingle(id)

    rowsUpdated match {
      case 1 =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"))
        Ok(Json.toJson(result))
      case 0 =>
        val result = Map("success" -> toJson(false), "msg" -> toJson("Zero rows affected means that your delete failed, fix it Enda"))
        Ok(Json.toJson(result))
    }
  }

  def insert(CompanyName:String) = Action{

    val newTest = models.Account(anorm.NotAssigned, CompanyName)
    val id = models.Account.insert(newTest)

    id match {
      case Some(autoIncrementId) =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(autoIncrementId))
        Ok(Json.toJson(result))
      case None =>
        // TODO inserts can fail; i need to handle this properly.
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"), "id" -> toJson(-1))
        Ok(Json.toJson(result))
    }
  }

  //  def listBars() = Action {
  //    val bars = Bar.findAll()
  //
  //    val json = Json.generate(bars)
  //
  //    Ok(json).as("application/json")
  //  }
}
