package models

import java.util.UUID

import models.ProductDetail.ProductDetailTable
import models.Transaction.TransactionTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class TransactionDetail(id: UUID = UUID.randomUUID(), transactionId: UUID, productDetailId: UUID, numberOfPurchases: Int, subTotalPrice: BigDecimal, status: Int)

object TransactionDetail extends ((UUID, UUID, UUID, Int, BigDecimal, Int) => TransactionDetail) {

  val transactions = TableQuery[TransactionTable]
  val productDetails = TableQuery[ProductDetailTable]

  class TransactionDetailTable(slickTag: SlickTag) extends SlickTable[TransactionDetail](slickTag, "transaction_detail") {
    def transactionIdFK = foreignKey("transaction_id", transactionId, transactions)(_.id)

    def transactionId = column[UUID]("transaction_id")

    def productDetailIdFK = foreignKey("product_detail_id", productDetailId, productDetails)(_.id)

    def productDetailId = column[UUID]("product_detail_id")

    def * = (id, transactionId, productDetailId, numberOfPurchases, subTotalPrice, status).mapTo[TransactionDetail]

    def id = column[UUID]("id", O.PrimaryKey)

    def numberOfPurchases = column[Int]("number_of_purchases")

    def subTotalPrice = column[BigDecimal]("subtotal_price")

    def status = column[Int]("status")
  }

}

object TransactionDetailJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val transactionDetailJsonProtocolFormat: JsonFormat[TransactionDetail] = jsonFormat6(TransactionDetail)
}