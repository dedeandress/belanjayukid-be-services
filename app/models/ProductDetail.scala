package models

import java.util.UUID

import models.Products.ProductsTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class ProductDetail(id: UUID, productStockName: String, productStockPrice: BigDecimal, productStockValue: Int, productId: UUID)

object ProductDetail extends ((UUID, String, BigDecimal, Int, UUID)=>ProductDetail) {

  val products = TableQuery[ProductsTable]

  class ProductDetailTable(slickTag: SlickTag) extends SlickTable[ProductDetail](slickTag, "product_detail") {
    def id = column[UUID]("id")
    def productStockName = column[String]("product_stock_name")
    def productStockPrice = column[BigDecimal]("product_stock_price")
    def productStockValue = column[Int]("product_stock_value")
    def productId = column[UUID]("product_id")
    def productIdFK = foreignKey("product_id", productId, products)(_.id)
    def * = (id, productStockName, productStockPrice, productStockValue, productId).mapTo[ProductDetail]
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

  implicit val productDetailJsonProtocolFormat: JsonFormat[ProductDetail] = jsonFormat5(ProductDetail)
}