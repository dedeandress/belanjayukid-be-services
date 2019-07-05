package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Product(id: UUID, productName: String, productStock: Int, categoryId: UUID)

object Product extends ((UUID, String, Int, UUID)=>Product) {
  class ProductTable(slickTag: SlickTag) extends SlickTable[Product](slickTag, "product"){
    def id = column[UUID]("id")
    def productName = column[String]("product_name")
    def productStock = column[Int]("product_stock")
    def categoryId = column[UUID]("category_id")
    def * = (id, productName, productStock, categoryId).mapTo[Product]
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

  implicit val productJsonProtocolFormat: JsonFormat[Product] = jsonFormat4(Product)
}
