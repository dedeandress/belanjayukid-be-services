package models

import java.util.UUID
import java.util.UUID.randomUUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class User(id: UUID = randomUUID, username: String, password: String, email: String, userProfileId: UUID)

object User extends ((UUID, String, String, String, UUID) => User) {

  import models.UserProfile.UserProfileTable

  val userProfiles = TableQuery[UserProfileTable]
  class UserTable(slickTag: SlickTag) extends SlickTable[User](slickTag, "users"){
    def id = column[UUID]("id", O.PrimaryKey)
    def username = column[String]("username")
    def password = column[String]("password")
    def email = column[String]("email")
    def userProfileId = column[UUID]("user_profile_id")
    def userProfileIdFK = foreignKey("user_profile_id", userProfileId, userProfiles)(_.id)
    def * = (id, username, password, email, userProfileId).mapTo[User]
  }
}

object UserJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val userJsonProtocolFormat: JsonFormat[User] = jsonFormat5(User)
}