package models

import java.util.UUID

import models.Customer.CustomerTable
import models.Staff.StaffTable
import models.Store.StoreTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Transaction(id: UUID, paymentStatus: Int, staffId: UUID, customerId: UUID, storeId: UUID, totalPrice: BigDecimal)

object Transaction extends ((UUID, Int, UUID, UUID, UUID, BigDecimal)=>Transaction) {

  val customers = TableQuery[CustomerTable]
  val staffs = TableQuery[StaffTable]
  val stores = TableQuery[StoreTable]

  class TransactionTable(slickTag: SlickTag) extends SlickTable[Transaction](slickTag, "transactions") {
    def id = column[UUID]("id", O.PrimaryKey)
    def paymentStatus = column[Int]("payment_status")
    def storeId = column[UUID]("store_id")
    def staffId = column[UUID]("staff_id")
    def customerId = column[UUID]("customer_id")
    def totalPrice = column[BigDecimal]("total_price")
    def staffIdFK = foreignKey("staff_id", staffId, staffs)(_.id)
    def storeIdFK = foreignKey("store_id", storeId, stores)(_.id)
    def customerIdFK = foreignKey("customer_id", customerId, customers)(_.id)
    def * = (id, paymentStatus, staffId, customerId, storeId, totalPrice).mapTo[Transaction]
  }

}

object TransactionJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val transactionsJsonProtocolFormat: JsonFormat[Transaction] = jsonFormat6(Transaction)
}