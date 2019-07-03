package graphql.schemas

import com.google.inject.Inject
import graphql.resolvers.{RoleResolver, UserProfileResolver, UserResolver}
import models.User
import sangria.macros.derive.{ObjectTypeName, deriveObjectType}
import sangria.schema._

class UserSchema @Inject()(userResolver: UserResolver, roleResolver: RoleResolver, userProfileResolver: UserProfileResolver){

  import java.util.UUID

  import models.{Role, UserProfile}
  import sangria.macros.derive.ReplaceField
  import utilities.CustomScalar

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))
  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("dateOfBirth", Field("dateOfBirth", CustomScalar.GraphQLDateTime, resolve = _.value.dateOfBirth)),
  )
  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ObjectTypeName("User"),
    ReplaceField("userProfileId",
      Field(
        "userProfile", UserProfileType, resolve = c => userProfileResolver.findUserProfile(c.value.userProfileId)
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
        Argument("id", CustomScalar.UUIDType),
      ),
      resolve = sangriaContext => userResolver.findUser(sangriaContext.args.arg[UUID]("id"))
    )
  )

  val Mutations: List[Field[Unit, Unit]] = List(
    Field(
      name = "addUser",
      fieldType = UserType,
      arguments = List(
        Argument("username", StringType),
        Argument("password", StringType),
        Argument("email", StringType),
        Argument("userProfileId", CustomScalar.UUIDType)
      ),
      resolve = sangriaContext => userResolver.addUser(sangriaContext.args.arg[String]("username"),
        sangriaContext.args.arg[String]("password"),
        sangriaContext.args.arg[String]("email"),
        sangriaContext.args.arg[UUID]("userProfileId")
      )
    ),
    Field(
      name = "deleteUser",
      fieldType = BooleanType,
      arguments = List(
        Argument("id", CustomScalar.UUIDType)
      ),
      resolve = sangriaContext => userResolver.deleteUser(sangriaContext.args.arg[UUID]("id"))
    ),
    Field(
      name = "updateUser",
      fieldType = UserType,
      arguments = List(
        Argument("id", CustomScalar.UUIDType),
        Argument("username", StringType),
        Argument("password", StringType),
        Argument("email", StringType),
        Argument("userProfileId", CustomScalar.UUIDType)
      ),
      resolve = sangriaContext =>
        userResolver.updateUser(
          sangriaContext.args.arg[UUID]("id"),
          sangriaContext.args.arg[String]("username"),
          sangriaContext.args.arg[String]("password"),
          sangriaContext.args.arg[String]("email"),
          sangriaContext.args.arg[UUID]("userProfileId")
        )
    )
  )

}
