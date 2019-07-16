package graphql.schemas

import akka.http.scaladsl.model.DateTime
import com.google.inject.Inject
import graphql.resolvers.{RoleResolver, UserProfileResolver, UserResolver}
import models.User
import sangria.schema._
import services.UserService
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

class UserSchema @Inject()(userResolver: UserResolver, roleResolver: RoleResolver, userProfileResolver: UserProfileResolver, userService: UserService){

  import java.util.UUID

  import models.{Role, UserProfile}
  import sangria.macros.derive._
  import utilities.CustomScalar

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))
  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("dateOfBirth", Field("dateOfBirth", CustomScalar.GraphQLDateTime, resolve = _.value.dateOfBirth)),
    ExcludeFields("userId")
  )
//  ExcludeFields("id"),
  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    AddFields(Field("userProfile", OptionType(UserProfileType) ,resolve = c => userProfileResolver.findUserProfile(c.value.id)))
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
    ),
    Field(
      name = "login",
      fieldType = StringType,
      arguments = List(
        Argument("username", StringType),
        Argument("password", StringType)
      ),
      resolve = sangriaContext => userResolver.login(sangriaContext.args.arg[String]("username"),sangriaContext.args.arg[String]("password"))
    )
  )
  import graphql.input.{UserInput, UserProfileInput}
  import models.Authorized
  import sangria.marshalling.sprayJson._
  import spray.json.DefaultJsonProtocol._

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

  implicit val userProfileJsonProtocolFormat: JsonFormat[UserProfileInput] = jsonFormat6(UserProfileInput)

  implicit val UserInputType : InputObjectType[UserInput] = deriveInputObjectType[UserInput]()
//  Field(
//    name = "loginUser",
//    fieldType = StringType,
//    arguments = List(
//      Argument("username", StringType),
//      Argument("password", StringType)
//    ),
//    resolve = ctx => UpdateCtx(
//      ctx.ctx.login(ctx.args.arg[String]("username"), ctx.args.arg[String]("password"))
//    )
//  ),
  val UserInputArg = Argument("user", UserInputType)
  implicit val userProfileInputType : InputObjectType[UserProfileInput] = deriveInputObjectType[UserProfileInput](
    ReplaceInputField("userId", InputField("userId", CustomScalar.UUIDType)),
    ReplaceInputField("dateOfBirth", InputField("dateOfBirth", CustomScalar.GraphQLDateTime))
  )
  val UserProfileInputArg = Argument("userProfile", userProfileInputType)
  // TODO: if you get error inputObjectResultInput(?: FromInput[...], ioArgType) you must import sangria spray json when you use InputType
  val Mutations: List[Field[Unit, Unit]] = List(
    Field(
      name = "inputUser",
      fieldType = UserType,
      arguments = UserInputArg :: Nil,
      tags = Authorized :: Nil,
      resolve = sangriaContext => userResolver.inputUser(sangriaContext.arg(UserInputArg))
    ),
    Field(
      name = "addUser",
      fieldType = UserType,
      arguments = List(
        Argument("username", StringType),
        Argument("password", StringType),
        Argument("email", StringType)
      ),
      resolve = sangriaContext => userResolver.addUser(new models.User(username = sangriaContext.args.arg[String]("username"),
        password=sangriaContext.args.arg[String]("password"),
        email = sangriaContext.args.arg[String]("email"))
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
    Field(
      name = "insertUserProfile",
      fieldType = UserProfileType,
      arguments = UserProfileInputArg :: Nil,
      resolve = sangriaContext => userProfileResolver.insertUserProfile(sangriaContext.arg(UserProfileInputArg))
    )
  )

}
