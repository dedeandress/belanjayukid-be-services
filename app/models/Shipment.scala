package models

import java.util.UUID

import models.Transaction.TransactionTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Shipment(id: UUID, address: String, phone: String, price: BigDecimal, transactionId: UUID)

object Shipment extends ((UUID, String, String, BigDecimal, UUID)=>Shipment){

  val transactions = TableQuery[TransactionTable]

  class ShipmentTable(slickTag: SlickTag) extends SlickTable[Shipment](slickTag, "shipment") {
    def id = column[UUID]("id", O.PrimaryKey)
    def address = column[String]("address")
    def phone = column[String]("address")
    def price = column[BigDecimal]("price")
    def transactionId = column[UUID]("transaction_id")
    def transactionIdFK = foreignKey("transaction_id", transactionId, transactions)(_.id)
    def * = (id, address, phone, price, transactionId).mapTo[Shipment]
  }

}

object ShipmentJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val shipmentJsonProtocolFormat: JsonFormat[Shipment] = jsonFormat5(Shipment)
}
