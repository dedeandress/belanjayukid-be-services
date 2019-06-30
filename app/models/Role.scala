package models

import slick.jdbc.H2Profile.api.{Table => SlickTable, _}
import slick.lifted.{Tag => SlickTag}
import spray.json.{DefaultJsonProtocol, JsonFormat}


case class Role(id: Option[Long] = None, name: String, description: String)

object Role extends ((Option[Long], String, String)=>Role){
  class RoleTable(slickTag: SlickTag) extends SlickTable[Role](slickTag, "roles"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def * = (id.?, name, description).mapTo[Role]
  }
}

object RoleJsonProtocol extends DefaultJsonProtocol{
  implicit val roleJsonProtocol: JsonFormat[Role] = jsonFormat3(Role)
}
