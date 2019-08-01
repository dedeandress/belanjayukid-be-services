package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class ProductStock(id: UUID=UUID.randomUUID(), name: String)

object ProductStock extends ((UUID, String)=>ProductStock){

  class ProductStockTable(slickTag: SlickTag) extends SlickTable[ProductStock](slickTag, "product_stock") {
    def id = column[UUID]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name).mapTo[ProductStock]
  }

}

object ProductStockJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val productStockJsonProtocolFormat: JsonFormat[ProductStock] = jsonFormat2(ProductStock)
}

