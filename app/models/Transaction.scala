package models

import java.time.ZonedDateTime
import java.util.UUID

import models.Customer.CustomerTable
import models.Payment.PaymentTable
import models.Staff.StaffTable
import models.Store.StoreTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.{PaymentStatus, TransactionStatus}

case class Transaction(id: UUID = UUID.randomUUID(), paymentStatus: Int = PaymentStatus.UNPAID, staffId: Option[UUID] = null, customerId: Option[UUID] = null, totalPrice: BigDecimal = 0, profit: BigDecimal = 0, status: Int = TransactionStatus.INITIAL, date: Long = ZonedDateTime.now().toEpochSecond, paymentId: UUID)

object Transaction extends ((UUID, Int, Option[UUID], Option[UUID], BigDecimal, BigDecimal, Int, Long, UUID) => Transaction) {

  val customers = TableQuery[CustomerTable]
  val staffs = TableQuery[StaffTable]
  val stores = TableQuery[StoreTable]
  val payments = TableQuery[PaymentTable]

  class TransactionTable(slickTag: SlickTag) extends SlickTable[Transaction](slickTag, "transactions") {
    def staffIdFK = foreignKey("staff_id", staffId, staffs)(_.id)

    def staffId = column[Option[UUID]]("staff_id")

    def customerIdFK = foreignKey("customer_id", customerId, customers)(_.id)

    def customerId = column[Option[UUID]]("customer_id")

    def paymentIdFK = foreignKey("payment_id", paymentId, payments)(_.id)

    def paymentId = column[UUID]("payment_id")

    def * = (id, paymentStatus, staffId, customerId, totalPrice, profit, status, date, paymentId).mapTo[Transaction]

    def id = column[UUID]("id", O.PrimaryKey)

    def paymentStatus = column[Int]("payment_status")

    def totalPrice = column[BigDecimal]("total_price")

    def profit = column[BigDecimal]("profit")

    def status = column[Int]("status")

    def date = column[Long]("date")
  }

}

object TransactionJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val transactionsJsonProtocolFormat: JsonFormat[Transaction] = jsonFormat9(Transaction)
}