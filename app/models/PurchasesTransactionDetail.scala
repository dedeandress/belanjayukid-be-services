package models

import java.util.UUID

import models.ProductDetail.ProductDetailTable
import models.PurchasesTransaction.PurchasesTransactionTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class PurchasesTransactionDetail(id: UUID = UUID.randomUUID(), purchasesTransactionId: UUID, productDetailId: UUID, numberOfPurchases: Int, status: Int)

object PurchasesTransactionDetail extends ((UUID, UUID, UUID, Int, Int) => PurchasesTransactionDetail) {

  val purchasesTransactions = TableQuery[PurchasesTransactionTable]
  val productDetails = TableQuery[ProductDetailTable]

  class PurchasesTransactionDetailTable(slickTag: SlickTag) extends SlickTable[PurchasesTransactionDetail](slickTag, "transaction_detail") {
    def purchasesTransactionIdFK = foreignKey("transaction_id", purchasesTransactionId, purchasesTransactions)(_.id)

    def purchasesTransactionId = column[UUID]("transaction_id")

    def productDetailIdFK = foreignKey("product_detail_id", productDetailId, productDetails)(_.id)

    def productDetailId = column[UUID]("product_detail_id")

    def * = (id, purchasesTransactionId, productDetailId, numberOfPurchases, status).mapTo[PurchasesTransactionDetail]

    def id = column[UUID]("id", O.PrimaryKey)

    def numberOfPurchases = column[Int]("number_of_purchases")

    def status = column[Int]("status")
  }

}

object PurchasesTransactionDetailJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val purchasesTransactionDetailJsonProtocolFormat: JsonFormat[PurchasesTransactionDetail] = jsonFormat5(PurchasesTransactionDetail)
}