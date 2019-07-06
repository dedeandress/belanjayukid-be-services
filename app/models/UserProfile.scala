package models

import java.util.UUID
import java.util.UUID.randomUUID

import akka.http.scaladsl.model.DateTime
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import java.sql.Timestamp
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class UserProfile(id: UUID = randomUUID, fullName: String, phoneNumber: String, address: String, noNik: String, dateOfBirth: DateTime)

object UserProfile extends ((UUID, String, String, String, String, DateTime) => UserProfile) {

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.clicks),
    ts => DateTime(ts.getTime)
  )
  class UserProfileTable(slickTag: SlickTag) extends SlickTable[UserProfile](slickTag, "user_profile"){
    def id = column[UUID]("id", O.PrimaryKey)
    def fullName = column[String]("full_name")
    def phoneNumber = column[String]("phone_number")
    def address = column[String]("address")
    def noNik = column[String]("no_nik")
    def dateOfBirth = column[DateTime]("date_of_birth")
    def * = (id, fullName, phoneNumber, address, noNik, dateOfBirth).mapTo[UserProfile]
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

  implicit val userProfileJsonProtocolFormat: JsonFormat[UserProfile] = jsonFormat6(UserProfile)
}
