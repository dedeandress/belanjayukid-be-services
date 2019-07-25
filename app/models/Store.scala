package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Store(id: UUID, storeName: String, phoneNumber: String, address: String)

object Store extends ((UUID, String, String, String)=> Store) {

  class StoreTable(slickTag : SlickTag) extends SlickTable[Store](slickTag, "store"){
    def id = column[UUID]("id", O.PrimaryKey)
    def storeName = column[String]("store_name")
    def phoneNumber = column[String]("phone_number")
    def address = column[String]("address")
    def * = (id, storeName, phoneNumber, address).mapTo[Store]
  }

}

object StoreJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val storeJsonProtocolFormat: JsonFormat[Store] = jsonFormat4(Store)
}