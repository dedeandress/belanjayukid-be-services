package graphql.schemas

import com.google.inject.Inject
import graphql.resolvers.UserResolver
import models.User
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema._

class UserSchema @Inject()(userResolver: UserResolver){


  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](ObjectTypeName("User"))

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
        Argument("email", StringType)
      ),
      resolve = sangriaContext => userResolver.addUser(sangriaContext.args.arg[String]("name"), sangriaContext.args.arg[String]("email"))
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
        Argument("email", StringType)
      ),
      resolve = sangriaContext =>
        userResolver.updateUser(
          sangriaContext.args.arg[Long]("id"),
          sangriaContext.args.arg[String]("name"),
          sangriaContext.args.arg[String]("email")
        )
    )
  )

}
