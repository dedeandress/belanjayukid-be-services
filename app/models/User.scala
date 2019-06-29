package models

import slick.jdbc.H2Profile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.DefaultJsonProtocol

case class User(id: Option[Long] = None, name: String, email: String )

object User extends ((Option[Long], String, String) => User) {
  class Table(slickTag: SlickTag) extends SlickTable[User](slickTag, "USERS"){
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def email = column[String]("EMAIL")
    def * = (id.?, name, email).mapTo[User]
  }
}

import spray.json.JsonFormat

object PostJsonProtocol extends DefaultJsonProtocol {
  implicit val postJsonProtocolFormat: JsonFormat[User] = jsonFormat3(User)
}