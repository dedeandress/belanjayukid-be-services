package models

import java.util.UUID

import models.Transaction.TransactionTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Payment(id: UUID = UUID.randomUUID(), transactionId: UUID, debt: BigDecimal = 0, amountOfPayment: BigDecimal)

object Payment extends ((UUID, UUID, BigDecimal, BigDecimal)=>Payment) {

  val transactions = TableQuery[TransactionTable]

  class PaymentTable(slickTag: SlickTag) extends SlickTable[Payment](slickTag, "payments") {

    def id = column[UUID]("id")
    def transactionId = column[UUID]("transaction_id")
    def debt = column[BigDecimal]("debt")
    def amountOfPayment = column[BigDecimal]("amount_of_payment")
    def * = (id, transactionId, debt, amountOfPayment).mapTo[Payment]
    def transactionIdFK = foreignKey("transaction_id", transactionId, transactions)(_.id)

  }

}

object PaymentJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val paymentJsonProtocolFormat: JsonFormat[Payment] = jsonFormat4(Payment)
}