package models

import java.util.UUID
import java.util.UUID.randomUUID

import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Role(id: UUID = randomUUID, name: String)

object Role extends ((UUID, String) => Role) {

  class RoleTable(slickTag: SlickTag) extends SlickTable[Role](slickTag, "role") {
    def * = (id, name).mapTo[Role]

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")
  }

}

object RoleJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val roleJsonProtocol: JsonFormat[Role] = jsonFormat2(Role)
}
