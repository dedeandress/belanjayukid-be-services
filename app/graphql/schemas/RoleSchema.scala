package graphql.schemas

import com.google.inject.Inject
import graphql.resolvers.RoleResolver
import models.Role
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema.ObjectType

class RoleSchema @Inject()(roleResolver: RoleResolver){

  import sangria.schema.{Field, ListType}


  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"))

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "roles",
      fieldType = ListType(RoleType),
      resolve = _ => roleResolver.roles
    )
  )
}
