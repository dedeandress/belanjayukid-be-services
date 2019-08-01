package models

import java.util.UUID

import models.Role.RoleTable
import models.User.UserTable
import slick.jdbc.PostgresProfile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

case class Staff(id: UUID = UUID.randomUUID(), userId: UUID, roleId: UUID)

object Staff extends ((UUID, UUID, UUID)=>Staff) {

  val users = TableQuery[UserTable]
  val roles = TableQuery[RoleTable]

  class StaffTable(slickTag: SlickTag) extends SlickTable[Staff](slickTag,"staff"){
    def id = column[UUID]("id", O.PrimaryKey)
    def userId = column[UUID]("user_id")
    def roleId = column[UUID]("role_id")
    def userIdFK = foreignKey("user_id", userId, users)(_.id)
    def roleIdFK = foreignKey("role_id", roleId, roles)(_.id)
    def * = (id, userId, roleId).mapTo[Staff]
  }

}

object StaffJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val staffJsonProtocolFormat: JsonFormat[Staff] = jsonFormat3(Staff)
}
