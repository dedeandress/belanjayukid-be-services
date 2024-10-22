package models

import java.util.UUID

import models.Category.CategoryTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Products(id: UUID = UUID.randomUUID(), SKU: String, name: String, stock: Int = 0, categoryId: UUID, imageUrl: String = "", status: Boolean = true)

object Products extends ((UUID, String, String, Int, UUID, String, Boolean) => Products) {

  val categories = TableQuery[CategoryTable]

  class ProductsTable(slickTag: SlickTag) extends SlickTable[Products](slickTag, "products") {
    def categoryIdFK = foreignKey("category_id", categoryId, categories)(_.id)

    def * = (id, SKU, name, stock, categoryId, imageUrl, status).mapTo[Products]

    def id = column[UUID]("id", O.PrimaryKey)

    def SKU = column[String]("sku")

    def name = column[String]("name")

    def stock = column[Int]("stock")

    def imageUrl = column[String]("image_url")

    def status = column[Boolean]("status")

    def categoryId = column[UUID]("category_id")
  }

}

object ProductJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val productJsonProtocolFormat: JsonFormat[Product] = jsonFormat7(Products)
}
