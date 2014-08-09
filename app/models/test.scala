package models;

import play.api.Logger
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
/**
* Created by endamccormack.
*/
case class Test(ID: Pk[Long], FirstName:Option[String], LastName:Option[String], Address: Option[String])
//case class Test(FirstName:Option[String], LastName:String, Address: String)
//case class Bar(id: Pk[Long], name: String)

object Test{

  val simple = {
    get[Pk[Long]]("id") ~
    get[Option[String]]("firstname") ~
    get[Option[String]]("lastname") ~
    get[Option[String]]("address") map {
      case id~firstname~lastname~address => Test(id,firstname,lastname, address)
      //case id~name => Bar(id, name)
    }
  }


  def findAll(): Seq[Test] = {
    DB.withConnection { implicit connection =>
      SQL("select * from daTest").as(Test.simple *)
    }
  }

  def insert(test: Test): Option[Long] = {
    val id: Option[Long] = DB.withConnection { implicit connection =>
      //var query = "insert into daTest( FirstName , LastName, Address ) values ('" + test.FirstName.getOrElse("Unknown") + "', '" + test.LastName + "', '" + test.Address + "')"
      //Logger.warn(query)
      //SQL(query).executeInsert()
      SQL("insert into daTest(FirstName, LastName, Address) values({fname},{sname}, {address})").on(
      "fname" -> test.FirstName,
      "sname" -> test.LastName,
      "address" -> test.Address
      ).executeInsert()
      //SQL("insert into daTest( FirstName , LastName, Address ) values ( '', '', '' )").executeInsert()
//      SQL( "insert into daTest( FirstName , LastName, Address ) values ( {'hon', 'han', 'dan' } ) ").on(
//        'hon -> test.FirstName.getOrElse("Unknown"),
//        'han -> test.LastName,
//        'dan -> test.Address
//      ).executeInsert()
    }
    Logger.warn(id.toString())
    id
  }
}