package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Customer(id: UUID, userId: UUID)

object Customer extends ((UUID, UUID)=>Customer) {
  class StaffTable(slickTag: SlickTag) extends SlickTable[Customer](slickTag,"customer"){
    def id = column[UUID]("id")
    def userId = column[UUID]("user_id")
    def * = (id, userId).mapTo[Customer]
  }
}

object CustomerJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val customerJsonProtocolFormat: JsonFormat[Customer] = jsonFormat2(Customer)
}
