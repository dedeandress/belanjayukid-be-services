package models

import java.time.ZonedDateTime
import java.util.UUID

import models.Payment.PaymentTable
import models.Staff.StaffTable
import models.Store.StoreTable
import models.Supplier.SupplierTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.{PaymentStatus, PurchasesTransactionStatus}

case class PurchasesTransaction(id: UUID = UUID.randomUUID(), paymentStatus: Int = PaymentStatus.UNPAID, staffId: Option[UUID] = null, supplierId: Option[UUID] = null, totalPrice: BigDecimal = 0, status: Int = PurchasesTransactionStatus.INITIAL, date: Long = ZonedDateTime.now().toEpochSecond, paymentId: UUID)

object PurchasesTransaction extends ((UUID, Int, Option[UUID], Option[UUID], BigDecimal, Int, Long, UUID) => PurchasesTransaction) {

  val suppliers = TableQuery[SupplierTable]
  val staffs = TableQuery[StaffTable]
  val stores = TableQuery[StoreTable]
  val payments = TableQuery[PaymentTable]

  class PurchasesTransactionTable(slickTag: SlickTag) extends SlickTable[PurchasesTransaction](slickTag, "purchases_transactions") {
    def staffIdFK = foreignKey("staff_id", staffId, staffs)(_.id)

    def staffId = column[Option[UUID]]("staff_id")

    def supplierIdFK = foreignKey("supplier_id", supplierId, suppliers)(_.id)

    def supplierId = column[Option[UUID]]("supplier_id")

    def paymentIdFK = foreignKey("payment_id", paymentId, payments)(_.id)

    def paymentId = column[UUID]("payment_id")

    def * = (id, paymentStatus, staffId, supplierId, totalPrice, status, date, paymentId).mapTo[PurchasesTransaction]

    def id = column[UUID]("id", O.PrimaryKey)

    def paymentStatus = column[Int]("payment_status")

    def totalPrice = column[BigDecimal]("total_price")

    def status = column[Int]("status")

    def date = column[Long]("date")
  }

}

object PurchasesTransactionJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val purchasesTransactionsJsonProtocolFormat: JsonFormat[PurchasesTransaction] = jsonFormat8(PurchasesTransaction)
}