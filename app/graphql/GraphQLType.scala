package graphql

import java.util.UUID

import akka.http.scaladsl.model.DateTime
import com.google.inject.Inject
import graphql.input.{StaffInput, UserInput, UserProfileInput}
import graphql.resolvers.{RoleResolver, StaffResolver, UserProfileResolver, UserResolver}
import models.{Role, Staff, User, UserProfile}
import sangria.macros.derive.{ReplaceInputField, _}
import sangria.marshalling.sprayJson._
import sangria.schema.{Argument, Field, InputField, InputObjectType, ObjectType, OptionType}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.CustomScalar

class GraphQLType @Inject()(userResolver: UserResolver, staffResolver: StaffResolver, userProfileResolver: UserProfileResolver, roleResolver: RoleResolver){

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))

  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ExcludeFields("userId")
  )

  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    AddFields(Field("userProfile", OptionType(UserProfileType) ,resolve = c => userProfileResolver.findUserProfile(c.value.id))),
    ExcludeFields("id")
  )

  implicit val StaffType: ObjectType[Unit, Staff] = deriveObjectType[Unit, Staff](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("userId", Field("user", OptionType(UserType), resolve = c => userResolver.findUser(c.value.userId))),
    ReplaceField("roleId", Field("role", OptionType(RoleType), resolve = c => roleResolver.findRole(c.value.roleId)))
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
  implicit val staffJsonProtocolFormat: JsonFormat[StaffInput] = jsonFormat3(StaffInput)
  implicit val userProfileInputType : InputObjectType[UserProfileInput] = deriveInputObjectType[UserProfileInput]()
  implicit val UserInputType : InputObjectType[UserInput] = deriveInputObjectType[UserInput]()
  val UserInputArg = Argument("user", UserInputType)
  val UserProfileInputArg = Argument("userProfile", userProfileInputType)

  implicit val StaffInputType: InputObjectType[StaffInput] = deriveInputObjectType[StaffInput](
    ReplaceInputField("userInput", InputField("userInput", UserInputType)),
    ReplaceInputField("userProfileInput", InputField("userProfileInput", userProfileInputType)),
  )

  val StaffInputArg = Argument("staff", StaffInputType)
}
