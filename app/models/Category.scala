package models

import java.util.UUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Category(id: UUID = UUID.randomUUID(), name: String)

object Category extends ((UUID, String)=>Category) {
  class CategoryTable(slickTag: SlickTag) extends SlickTable[Category](slickTag, "category") {
    def id = column[UUID]("id", O.PrimaryKey)
    def name = column[String]("category_name")
    def * = (id, name).mapTo[Category]
  }
}

object CategoryJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val categoryJsonProtocolFormat: JsonFormat[Category] = jsonFormat2(Category)
}
