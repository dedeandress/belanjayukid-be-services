package graphql

import java.util.UUID

import akka.http.scaladsl.model.DateTime
import com.google.inject.Inject
import graphql.input.{ProductDetailInput, ProductInput, StaffInput, UserInput, UserProfileInput}
import models.{Category, LoginUser, ProductDetail, ProductStock, Products, Role, Staff, User, UserProfile}
import repositories.repositoryInterfaces.{CategoryRepository, ProductDetailRepository, ProductStockRepository, ProductsRepository, RoleRepository, UserProfileRepository, UserRepository}
import sangria.macros.derive.{ReplaceInputField, _}
import sangria.marshalling.sprayJson._
import sangria.schema.{Argument, Field, InputField, InputObjectType, ListType, ObjectType, OptionType}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.CustomScalar

class GraphQLType @Inject()(userRepository: UserRepository
                            , userProfileRepository: UserProfileRepository, roleRepository: RoleRepository
                            , categoryRepository: CategoryRepository, productStockRepository: ProductStockRepository
                            , productRepository: ProductsRepository, productDetailRepository: ProductDetailRepository){

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))

  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ExcludeFields("userId")
  )

  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    AddFields(Field("userProfile", OptionType(UserProfileType) ,resolve = c => userProfileRepository.findById(c.value.id))),
    ExcludeFields("id")
  )

  implicit val StaffType: ObjectType[Unit, Staff] = deriveObjectType[Unit, Staff](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("userId", Field("user", OptionType(UserType), resolve = c => userRepository.find(c.value.userId))),
    ReplaceField("roleId", Field("role", OptionType(RoleType), resolve = c => roleRepository.findById(c.value.roleId)))
  )

  implicit val CategoryType: ObjectType[Unit, Category] = deriveObjectType[Unit, Category](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id))
  )

  implicit val ProductStockType: ObjectType[Unit, ProductStock] = deriveObjectType[Unit, ProductStock](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id))
  )

  implicit val ProductDetailType: ObjectType[Unit, ProductDetail] = deriveObjectType[Unit, ProductDetail](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("productStockId",
      Field(
        "productStock"
        , OptionType(ProductStockType)
        , resolve = c => productStockRepository.findProductStock(c.value.productStockId)
      )
    ),
    ReplaceField("sellingPrice", Field("sellingPrice", CustomScalar.BigDecimalType, resolve = _.value.sellingPrice)),
    ReplaceField("purchasePrice", Field("purchasePrice", CustomScalar.BigDecimalType, resolve = _.value.purchasePrice)),
    ReplaceField("productId",
      Field(
        "product"
        , OptionType(ProductType)
        , resolve = c => productRepository.findProduct(c.value.id)
      )
    )
  )

  implicit val ProductType: ObjectType[Unit, Products] = deriveObjectType[Unit, Products](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("categoryId", Field("category", OptionType(CategoryType), resolve = c => categoryRepository.findCategory(c.value.categoryId))),
    AddFields(
      Field("productDetail", ListType(ProductDetailType), resolve = c => productDetailRepository.findProductDetailByProductId(c.value.id))
    )
  )

  implicit val LoginUserType: ObjectType[Unit, LoginUser] = deriveObjectType[Unit, LoginUser](
    ObjectTypeName("Credential")
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

  implicit val userProfileInputJsonProtocolFormat: JsonFormat[UserProfileInput] = jsonFormat5(UserProfileInput)
  implicit val staffInputJsonProtocolFormat: JsonFormat[StaffInput] = jsonFormat3(StaffInput)
  implicit val productDetailInputJsonProtocolFormat: JsonFormat[ProductDetailInput] = jsonFormat4(ProductDetailInput)
  implicit val productInputJsonProtocolFormat: JsonFormat[ProductInput] = jsonFormat5(ProductInput)
  implicit val userProfileInputType : InputObjectType[UserProfileInput] = deriveInputObjectType[UserProfileInput]()
  implicit val UserInputType : InputObjectType[UserInput] = deriveInputObjectType[UserInput]()
  implicit val productInputType : InputObjectType[ProductInput] = deriveInputObjectType[ProductInput]()
  implicit val productDetailInputType : InputObjectType[ProductDetailInput] = deriveInputObjectType[ProductDetailInput]()

  val UserInputArg = Argument("user", UserInputType)
  val UserProfileInputArg = Argument("userProfile", userProfileInputType)
  val ProductInputArg = Argument("product", productInputType)
  val ProductDetailInputArg = Argument("productDetail", productDetailInputType)

  implicit val StaffInputType: InputObjectType[StaffInput] = deriveInputObjectType[StaffInput](
    ReplaceInputField("userInput", InputField("userInput", UserInputType)),
    ReplaceInputField("userProfileInput", InputField("userProfileInput", userProfileInputType)),
  )

  val StaffInputArg = Argument("staff", StaffInputType)
}
