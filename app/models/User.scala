package models

import java.util.UUID
import java.util.UUID.randomUUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class User(id: UUID = randomUUID, username: String = "BelanjaYukUser", password: String = "123456789", email: String = "user@belanjayuk.com")

object User extends ((UUID, String, String, String) => User) {

  class UserTable(slickTag: SlickTag) extends SlickTable[User](slickTag, "users") {
    def * = (id, username, password, email).mapTo[User]

    def id = column[UUID]("id", O.PrimaryKey)

    def username = column[String]("username")

    def password = column[String]("password")

    def email = column[String]("email")
  }

}

object UserJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val userJsonProtocolFormat: JsonFormat[User] = jsonFormat4(User)
}