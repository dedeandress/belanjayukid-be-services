package models

import java.util.UUID

import models.Category.CategoryTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Products(id: UUID = UUID.randomUUID(), productSKU: String,productName: String, productStock: Int, categoryId: UUID)

object Products extends ((UUID, String, String, Int, UUID)=>Products) {

  val categories = TableQuery[CategoryTable]

  class ProductsTable(slickTag: SlickTag) extends SlickTable[Products](slickTag, "products"){
    def id = column[UUID]("id")
    def productSKU = column[String]("product_sku")
    def productName = column[String]("product_name")
    def productStock = column[Int]("product_stock")
    def categoryId = column[UUID]("category_id")
    def categoryIdFK = foreignKey("category_id", categoryId, categories)(_.id)
    def * = (id, productName, productStock, categoryId).mapTo[Products]
  }
}

object ProductJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val productJsonProtocolFormat: JsonFormat[Product] = jsonFormat5(Products)
}