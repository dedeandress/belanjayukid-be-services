package graphql.schemas

import com.google.inject.Inject
import graphql.resolvers.{RoleResolver, UserResolver}
import models.User
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema._

class UserSchema @Inject()(userResolver: UserResolver, roleResolver: RoleResolver){

  import models.Role
  import sangria.macros.derive.AddFields

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"))

  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    AddFields(
      Field(
        "role", ListType(RoleType), resolve = c => roleResolver.findRole(c.value.roleId)
      )
    )
  )

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "users",
      fieldType = ListType(UserType),
      resolve = _ => userResolver.users
    ),
    Field(
      name = "findUser",
      fieldType = OptionType(UserType),
      arguments = List(
        Argument("id", LongType),
      ),
      resolve = sangriaContext => userResolver.findUser(sangriaContext.args.arg[Long]("id"))
    )
  )

  val Mutations: List[Field[Unit, Unit]] = List(
    Field(
      name = "addUser",
      fieldType = UserType,
      arguments = List(
        Argument("name", StringType),
        Argument("email", StringType),
        Argument("roleId", LongType)
      ),
      resolve = sangriaContext => userResolver.addUser(sangriaContext.args.arg[String]("name"),
        sangriaContext.args.arg[String]("email"),
        sangriaContext.args.arg[Long]("roleId")
      )
    ),
    Field(
      name = "deleteUser",
      fieldType = BooleanType,
      arguments = List(
        Argument("id", LongType)
      ),
      resolve = sangriaContext => userResolver.deleteUser(sangriaContext.args.arg[Long]("id"))
    ),
    Field(
      name = "updateUser",
      fieldType = UserType,
      arguments = List(
        Argument("id", LongType),
        Argument("name", StringType),
        Argument("email", StringType),
        Argument("roleId", LongType)
      ),
      resolve = sangriaContext =>
        userResolver.updateUser(
          sangriaContext.args.arg[Long]("id"),
          sangriaContext.args.arg[String]("name"),
          sangriaContext.args.arg[String]("email"),
          sangriaContext.args.arg[Long]("roleId")
        )
    )
  )

}
