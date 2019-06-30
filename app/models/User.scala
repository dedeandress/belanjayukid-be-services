package models

import slick.jdbc.H2Profile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.DefaultJsonProtocol
import models.Role.RoleTable

case class User(id: Option[Long] = None, name: String, email: String, roleId: Long)

object User extends ((Option[Long], String, String, Long) => User) {
  val roles = TableQuery[RoleTable]
  class UserTable(slickTag: SlickTag) extends SlickTable[User](slickTag, "users"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def roleId = column[Long]("role_id")
    def roleIdFK = foreignKey("roleId_FK", roleId, roles)(_.id)
    def * = (id.?, name, email, roleId).mapTo[User]
  }
}

import spray.json.JsonFormat

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userJsonProtocolFormat: JsonFormat[User] = jsonFormat4(User)
}