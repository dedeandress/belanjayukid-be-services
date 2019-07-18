package graphql.schemas

import com.google.inject.Inject
import graphql.resolvers.RoleResolver
import models.Role
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema.ObjectType
import sangria.macros.derive.ReplaceField
import sangria.schema.{Field, ListType}
import utilities.CustomScalar

class RoleSchema @Inject()(roleResolver: RoleResolver){

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "roles",
      fieldType = ListType(RoleType),
      resolve = _ => roleResolver.roles
    )
  )
}
