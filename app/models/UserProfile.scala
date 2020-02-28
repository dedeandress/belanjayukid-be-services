package models

import java.util.UUID
import java.util.UUID.randomUUID

import akka.http.scaladsl.model.DateTime
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class UserProfile(id: UUID = randomUUID, fullName: String, phoneNumber: String, address: String, noNik: String, dateOfBirth: Long, userId: UUID)

object UserProfile extends ((UUID, String, String, String, String, Long, UUID) => UserProfile) {

  class UserProfileTable(slickTag: SlickTag) extends SlickTable[UserProfile](slickTag, "user_profile") {

    import models.User.UserTable

    val users = TableQuery[UserTable]

    def userIdFK = foreignKey("user_id", userId, users)(_.id)

    def * = (id, fullName, phoneNumber, address, noNik, dateOfBirth, userId).mapTo[UserProfile]

    def userId = column[UUID]("user_id")

    def id = column[UUID]("id", O.PrimaryKey)

    def fullName = column[String]("full_name")

    def phoneNumber = column[String]("phone_number")

    def address = column[String]("address")

    def noNik = column[String]("no_nik")

    def dateOfBirth = column[Long]("date_of_birth")
  }

}

object UserProfileJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    def write(x: DateTime) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => DateTime.fromIsoDateTimeString(x).get
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val userProfileJsonProtocolFormat: JsonFormat[UserProfile] = jsonFormat7(UserProfile)
}
