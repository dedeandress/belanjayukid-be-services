package graphql

import java.util.UUID

import akka.http.scaladsl.model.DateTime
import com.google.inject.Inject
import graphql.`type`.{ProductsResult, TransactionsResult}
import graphql.input._
import models._
import repositories.repositoryInterfaces._
import sangria.macros.derive.{ReplaceInputField, _}
import sangria.marshalling.sprayJson._
import sangria.schema.{Argument, Field, InputField, InputObjectType, ListType, ObjectType, OptionType}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}
import utilities.CustomScalar

class GraphQLType @Inject()(userRepository: UserRepository
                            , userProfileRepository: UserProfileRepository, roleRepository: RoleRepository
                            , categoryRepository: CategoryRepository, productStockRepository: ProductStockRepository
                            , productRepository: ProductsRepository, productDetailRepository: ProductDetailRepository
                            , transactionDetailRepository: TransactionDetailRepository, staffRepository: StaffRepository) {

  implicit val RoleType: ObjectType[Unit, Role] = deriveObjectType[Unit, Role](ObjectTypeName("Role"), ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)))

  implicit val UserProfileType: ObjectType[Unit, UserProfile] = deriveObjectType[Unit, UserProfile](ObjectTypeName("UserProfile"),
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ExcludeFields("userId")
  )

  implicit val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User](
    ObjectTypeName("User"),
    AddFields(Field("userProfile", OptionType(UserProfileType), resolve = c => userProfileRepository.findByUserId(c.value.id))),
    ExcludeFields("id")
  )

  implicit val CustomerType: ObjectType[Unit, Customer] = deriveObjectType[Unit, Customer](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("userId", Field("user", OptionType(UserType), resolve = c => userRepository.find(c.value.userId))),
  )

  implicit val StaffType: ObjectType[Unit, Staff] = deriveObjectType[Unit, Staff](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("userId", Field("user", OptionType(UserType), resolve = c => userRepository.find(c.value.userId))),
    ReplaceField("roleId", Field("role", OptionType(RoleType), resolve = c => roleRepository.findById(c.value.roleId)))
  )

  implicit val ProductType: ObjectType[Unit, Products] = deriveObjectType[Unit, Products](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("categoryId", Field("category", OptionType(CategoryType), resolve = c => categoryRepository.findCategory(c.value.categoryId))),
    AddFields(
      Field("productDetail", ListType(ProductDetailType), resolve = c => productDetailRepository.findProductDetailByProductId(c.value.id))
    )
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
        , resolve = c => productRepository.findProduct(c.value.productId)
      )
    )
  )

  implicit val TransactionDetailType: ObjectType[Unit, TransactionDetail] = deriveObjectType[Unit, TransactionDetail](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    ReplaceField("transactionId", Field("transactionID", CustomScalar.UUIDType, resolve = _.value.transactionId)),
    ReplaceField("productDetailId", Field("productDetail", OptionType(ProductDetailType), resolve = c => productDetailRepository.findProductDetail(c.value.productDetailId)))
  )

  implicit val TransactionType: ObjectType[Unit, Transaction] = deriveObjectType[Unit, Transaction](
    ReplaceField("id", Field("id", CustomScalar.UUIDType, resolve = _.value.id)),
    AddFields(
      Field("transactionDetail", ListType(TransactionDetailType), resolve = c => transactionDetailRepository.findTransactionDetailByTransactionId(c.value.id))
    ),
    ReplaceField("staffId", Field("staff", OptionType(StaffType), resolve = c => staffRepository.findById(c.value.staffId.get))),
    ReplaceField("customerId", Field("customer", OptionType(StaffType), resolve = c => staffRepository.findById(c.value.staffId.get)))
  )

  implicit val ProductsResultType: ObjectType[Unit, ProductsResult] = deriveObjectType[Unit, ProductsResult](
    ReplaceField("products",
      Field("product", ListType(ProductType), resolve = c => c.value.products)
    )
  )

  implicit val TransactionsResultType: ObjectType[Unit, TransactionsResult] = deriveObjectType[Unit, TransactionsResult](
    ReplaceField("transactions",
      Field("transactions", ListType(TransactionType), resolve = c => c.value.transactions)

    )
  )

  implicit val TransactionResultType: ObjectType[Unit, TransactionResult] = deriveObjectType[Unit, TransactionResult](
    ReplaceField("details",
      Field("details", ListType(TransactionDetailType), resolve = c => c.value.details)
    )
  )

  implicit val CreateTransactionResultType: ObjectType[Unit, CreateTransactionResult] = deriveObjectType[Unit, CreateTransactionResult](
    ReplaceField("transactionId",
      Field("transactionId", CustomScalar.UUIDType, resolve = c => c.value.transactionId)
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
  implicit val transactionDetailInputJsonProtocolFormat: JsonFormat[TransactionDetailInput] = jsonFormat3(TransactionDetailInput)
  implicit val transactionInputJsonProtocolFormat: JsonFormat[TransactionInput] = jsonFormat4(TransactionInput)
  implicit val productDetailInputJsonProtocolFormat: JsonFormat[ProductDetailInput] = jsonFormat5(ProductDetailInput)
  implicit val productInputJsonProtocolFormat: JsonFormat[ProductInput] = jsonFormat5(ProductInput)
  implicit val userProfileInputType: InputObjectType[UserProfileInput] = deriveInputObjectType[UserProfileInput]()
  implicit val UserInputType: InputObjectType[UserInput] = deriveInputObjectType[UserInput]()
  implicit val productInputType: InputObjectType[ProductInput] = deriveInputObjectType[ProductInput]()
  implicit val productDetailInputType: InputObjectType[ProductDetailInput] = deriveInputObjectType[ProductDetailInput]()
  implicit val transactionInputType: InputObjectType[TransactionInput] = deriveInputObjectType[TransactionInput]()
  implicit val transactionDetailInputType: InputObjectType[TransactionDetailInput] = deriveInputObjectType[TransactionDetailInput]()
  implicit val StaffInputType: InputObjectType[StaffInput] = deriveInputObjectType[StaffInput](
    ReplaceInputField("userInput", InputField("userInput", UserInputType)),
    ReplaceInputField("userProfileInput", InputField("userProfileInput", userProfileInputType)),
  )

  val UserInputArg = Argument("user", UserInputType)
  val UserProfileInputArg = Argument("userProfile", userProfileInputType)
  val ProductInputArg = Argument("product", productInputType)
  val ProductDetailInputArg = Argument("productDetail", productDetailInputType)
  val TransactionDetailInputArg = Argument("transactionDetail", transactionDetailInputType)
  val TransactionInputArg = Argument("transaction", transactionInputType)
  val CustomerInputArg = Argument("customer", userProfileInputType)

  val StaffInputArg = Argument("staff", StaffInputType)
}
