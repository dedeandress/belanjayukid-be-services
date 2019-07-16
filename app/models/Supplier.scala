package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Supplier(id: UUID, supplierName: String, phoneNumber: String, address: String)

object Supplier extends ((UUID, String, String, String)=>Supplier){

  class SupplierTable(slickTag: SlickTag) extends SlickTable[Supplier](slickTag, "supplier"){
    def id = column[UUID]("id")
    def supplierName = column[String]("supplier_name")
    def phoneNumber = column[String]("phone_number")
    def supplierAddress = column[String]("supplier_address")
    def * = (id, supplierName, phoneNumber, supplierAddress).mapTo[Supplier]
  }

}

object SupplierJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val supplierJsonProtocolFormat: JsonFormat[Supplier] = jsonFormat4(Supplier)
}
