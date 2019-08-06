package graphql.schemas

import akka.http.scaladsl.model.DateTime
import com.google.inject.Inject
import graphql.resolvers.{RoleResolver, UserProfileResolver, UserResolver}
import models.{Authorized, Role, Staff, User, UserProfile}
import sangria.schema._
import services.UserService
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import java.util.UUID

import sangria.macros.derive._
import utilities.CustomScalar
import graphql.input.{UserInput, UserProfileInput}
import sangria.marshalling.sprayJson._
import spray.json.DefaultJsonProtocol._

class UserSchema @Inject()(userResolver: UserResolver, roleResolver: RoleResolver, userProfileResolver: UserProfileResolver, userService: UserService){

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))
  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ExcludeFields("userId")
  )
//  ExcludeFields("id"),
  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    AddFields(Field("userProfile", OptionType(UserProfileType) ,resolve = c => userProfileResolver.userProfile(c.value.id)))
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
      resolve = sangriaContext => userResolver.user(sangriaContext.args.arg[UUID]("id"))
    )
  )

  implicit val userJsonProtocolFormat: JsonFormat[UserInput] = jsonFormat3(UserInput)
  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {
    def write(x: DateTime) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => DateTime.fromIsoDateTimeString(x).get
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val userProfileJsonProtocolFormat: JsonFormat[UserProfileInput] = jsonFormat5(UserProfileInput)

  implicit val UserInputType : InputObjectType[UserInput] = deriveInputObjectType[UserInput]()

  val UserInputArg = Argument("user", UserInputType)
  implicit val userProfileInputType : InputObjectType[UserProfileInput] = deriveInputObjectType[UserProfileInput]()
  val UserProfileInputArg = Argument("userProfile", userProfileInputType)
  // TODO: if you get error inputObjectResultInput(?: FromInput[...], ioArgType) you must import sangria spray json when you use InputType
  val Mutations: List[Field[Unit, Unit]] = List(
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
        Argument("email", StringType)
      ),
      resolve = sangriaContext =>
        userResolver.updateUser(
          sangriaContext.args.arg[UUID]("id"),
          sangriaContext.args.arg[String]("username"),
          sangriaContext.args.arg[String]("password"),
          sangriaContext.args.arg[String]("email")
        )
    ),
  )

}
