package controllers.API

import anorm.Pk
import controllers.API.AccountAPI._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Action, Controller}

/**
 * Created by endamccormack on 11/08/2014.
 */
object UserAPI extends Controller{

  def getAll = Action {
    val users = models.User.findAll()

    Ok(Json.toJson(users))
  }

  def getSingle(email:String) = Action{
    val user = models.User.findSingle(email)

    Ok(Json.toJson(user))
  }

  def deleteSingle(email:String) = Action{

    val rowsUpdated = models.User.deleteSingle(email)

    rowsUpdated match {
      case 1 =>
        val result = Map("success" -> toJson(true), "msg" -> toJson("Success!"))
        Ok(Json.toJson(result))
      case 0 =>
        val result = Map("success" -> toJson(false), "msg" -> toJson("Zero rows affected means that your delete failed, fix it Enda"))
        Ok(Json.toJson(result))
    }
  }

  def insert(EmailAddress:String,
             FirstName:String,
             LastName:String,
             JobTitle:String,
             Role:String,
             Account_id:Long,
             password:String) = Action{

    val newUser = models.User(anorm.Id(EmailAddress), Some(FirstName), Some(LastName), Some(JobTitle), Some(Role), Account_id, password )
    val id = models.User.insert(newUser)

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
}