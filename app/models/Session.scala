package models

import java.util.UUID

import akka.http.scaladsl.model.DateTime
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}


case class Session(id: UUID, secretToken: String, secretTokenExp: DateTime, userId: UUID)

object Session extends ((UUID, String, DateTime, UUID)=>Session) {

  import java.sql.Timestamp

  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.clicks),
    ts => DateTime(ts.getTime)
  )

  class SessionTable (slickTag: SlickTag) extends SlickTable[Session](slickTag, "session"){
    def id = column[UUID]("id")
    def secretToken = column[String]("secret_token")
    def secretTokenExp = column[DateTime]("secret_token_exp")
    def userId = column[UUID]("user_id")
    def * = (id, secretToken, secretTokenExp, userId).mapTo[Session]
  }
}

object SessionJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    def write(x: DateTime) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => DateTime.fromIsoDateTimeString(x).get
      case x => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

  implicit val categoryJsonProtocolFormat: JsonFormat[Session] = jsonFormat4(Session)
}