package models

import java.util.UUID

import models.ProductDetail.ProductDetailTable
import models.Transaction.TransactionTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class TransactionDetail(id: UUID, transactionId: UUID, productDetailId: UUID, numberOfPurchases: Int, subTotalPrice: BigDecimal)

object TransactionDetail extends ((UUID, UUID, UUID, Int, BigDecimal)=>TransactionDetail){

  val transactions = TableQuery[TransactionTable]
  val productDetails = TableQuery[ProductDetailTable]

  class TransactionDetailTable(slickTag: SlickTag) extends SlickTable[TransactionDetail](slickTag, "transaction_detail") {
    def id = column[UUID]("id")
    def transactionId = column[UUID]("transaction_id")
    def productDetailId = column[UUID]("product_detail_id")
    def numberOfPurchases = column[Int]("number_of_purchases")
    def subTotalPrice = column[BigDecimal]("subtotal_price")
    def transactionIdFK = foreignKey("transaction_id", transactionId, transactions)(_.id)
    def productDetailIdFK = foreignKey("product_detail_id", productDetailId, productDetails)(_.id)
    def * = (id, transactionId, productDetailId, numberOfPurchases, subTotalPrice).mapTo[TransactionDetail]
  }
}

object TransactionDetailJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val transactionDetailJsonProtocolFormat: JsonFormat[TransactionDetail] = jsonFormat5(TransactionDetail)
}