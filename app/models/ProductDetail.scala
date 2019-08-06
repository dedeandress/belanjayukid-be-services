package models

import java.util.UUID

import models.Products.ProductsTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.QueryUtility

case class ProductDetail(id: UUID = UUID.randomUUID(), productStockId: UUID, sellingPrice: BigDecimal, purchasePrice: BigDecimal, value: Int, status: Boolean = true, productId: UUID)

object ProductDetail extends ((UUID, UUID, BigDecimal, BigDecimal, Int, Boolean, UUID)=>ProductDetail) {

  val products = TableQuery[ProductsTable]

  class ProductDetailTable(slickTag: SlickTag) extends SlickTable[ProductDetail](slickTag, "product_detail") {
    def id = column[UUID]("id", O.PrimaryKey)
    def productStockId = column[UUID]("product_stock_id")
    def sellingPrice = column[BigDecimal]("selling_price")
    def purchasePrice = column[BigDecimal]("purchase_price")
    def value = column[Int]("value")
    def status = column[Boolean]("status")
    def productId = column[UUID]("product_id")
    def productIdFK = foreignKey("product_id", productId, products)(_.id)
    def productStockIdFK = foreignKey("product_stock_id", productStockId, QueryUtility.productStockQuery)(_.id)
    def * = (id, productStockId, sellingPrice, purchasePrice, value, status, productId).mapTo[ProductDetail]
  }
}

object ProductDetailJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val productDetailJsonProtocolFormat: JsonFormat[ProductDetail] = jsonFormat7(ProductDetail)
}