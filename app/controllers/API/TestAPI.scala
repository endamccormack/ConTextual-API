package controllers.API


import controllers.Application._
import play.Logger
import play.api.db._
import anorm._
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import anorm.SqlParser._
//import com.codahale.jerkson.Json
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import anorm.{~, Pk}
import anorm._
import anorm.SqlParser._
import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

/**
 * Created by endamccormack on 08/08/2014.
 */
object TestAPI extends Controller{

//  db.default.logStatements=true
//  logger.com.jolbox=DEBUG // for EBean

  def index = Action {
    //val all = models.Test.findAll()
    val users = models.User.findAll()

    Ok(views.html.index(users.toString()))
  }

  def insert(firstName:String,lastName:String,address:String) = Action{


    //val newTest = models.Test(NotAssigned, Some(firstName), Some(lastName), Some(address))


    /* user test (depends on account) */
    val password = "1Password2"
    val dasha256 = password.sha256
    val newTest = models.User(anorm.Id("Enda@gmail.com"), Some("Enda"), Some("McCormack"), Some("Helicopter"), Some("admin"), 1, dasha256)
    val id = models.User.insert(newTest)

    //account test
//    val newTest = models.Account(anorm.NotAssigned, "EndaCorp")
//    val id = models.Account.insert(newTest)

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
