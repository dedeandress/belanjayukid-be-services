package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Supplier(id: UUID, name: String, phoneNumber: String, address: String, status: Boolean = true)

object Supplier extends ((UUID, String, String, String, Boolean) => Supplier) {

  class SupplierTable(slickTag: SlickTag) extends SlickTable[Supplier](slickTag, "supplier") {
    def * = (id, name, phoneNumber, supplierAddress, status).mapTo[Supplier]

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")

    def phoneNumber = column[String]("phone_number")

    def supplierAddress = column[String]("address")

    def status = column[Boolean]("status")
  }

}

object SupplierJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val supplierJsonProtocolFormat: JsonFormat[Supplier] = jsonFormat5(Supplier)
}

