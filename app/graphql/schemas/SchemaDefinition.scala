package graphql.schemas

import java.util.UUID

import com.google.inject.Inject
import graphql.resolvers._
import graphql.{Context, GraphQLType}
import models.{Category, ProductStock}
import sangria.schema
import sangria.schema.{Argument, Field, ListType, OptionType}
import utilities.CustomScalar


class SchemaDefinition @Inject()(staffResolver: StaffResolver
                                 , userResolver: UserResolver, userProfileResolver: UserProfileResolver
                                 , roleResolver: RoleResolver, categoryResolver: CategoryResolver
                                 , productResolver: ProductResolver, productStockResolver: ProductStockResolver
                                 , productDetailResolver: ProductDetailResolver, transactionResolver: TransactionResolver
                                 , graphQLType: GraphQLType) {

  val Queries: List[Field[Context, Unit]] = List(
    Field(
      name = "categories",
      fieldType = ListType(graphQLType.CategoryType),
      resolve = sangriaContext => categoryResolver.categories(sangriaContext.ctx)
    ),
    Field(
      name = "productStocks",
      fieldType = ListType(graphQLType.ProductStockType),
      resolve = sangriaContext => productStockResolver.productStocks(sangriaContext.ctx)
    ),
    Field(
      name = "roles",
      fieldType = ListType(graphQLType.RoleType),
      resolve = sangriaContext => staffResolver.roles(sangriaContext.ctx)
    ),
    //product
    Field(
      name = "product",
      fieldType = OptionType(graphQLType.ProductType),
      arguments = List(
        Argument("productId", schema.StringType)
      ),
      resolve = sangriaContext => productResolver.product(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("productId")))
    ),
    Field(
      name = "products",
      fieldType = ListType(graphQLType.ProductType),
      resolve = sangriaContext => productResolver.products(sangriaContext.ctx)
    ),
    //transaction
    Field(
      name = "transactions",
      fieldType = ListType(graphQLType.TransactionType),
      arguments = List(
        Argument("status", schema.IntType)
      ),
      resolve = sangriaContext => transactionResolver.getTransactions(sangriaContext.ctx, sangriaContext.arg[Int]("status"))
    ),
    Field(
      name = "transaction",
      fieldType = OptionType(graphQLType.TransactionType),
      arguments = List(
        Argument("transactionId", schema.StringType)
      ),
      resolve = sangriaContext => transactionResolver.getTransaction(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("transactionId")))
    ),
    //staff
    Field(
      name = "staffs",
      fieldType = ListType(graphQLType.StaffType),
      resolve = sangriaContext => staffResolver.findAll(sangriaContext.ctx)
    ),
    Field(
      name = "staff",
      fieldType = OptionType(graphQLType.StaffType),
      arguments = List(
        Argument("staffId", schema.StringType)
      ),
      resolve = sangriaContext => staffResolver.findStaffById(sangriaContext.ctx, sangriaContext.arg[String]("staffId"))
    ),
    //product detail
    Field(
      name = "productDetails",
      fieldType = ListType(graphQLType.ProductDetailType),
      arguments = List(
        Argument("productId", schema.StringType)
      ),
      resolve = sangriaContext => productDetailResolver.productDetailByProductId(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("productId")))
    )
  )

  val Mutations: List[Field[Context, Unit]] = List(
    Field(
      name = "login",
      fieldType = graphQLType.LoginUserType,
      arguments = List(
        Argument("username", schema.StringType),
        Argument("password", schema.StringType)
      ),
      resolve = sangriaContext => staffResolver.login(sangriaContext.arg[String]("username"), sangriaContext.arg[String]("password"))
    ),
    Field(
      name = "createStaff",
      fieldType = OptionType(graphQLType.StaffType),
      arguments = graphQLType.StaffInputArg :: Nil,
      resolve = sangriaContext => staffResolver.createStaff(sangriaContext.ctx, sangriaContext.arg(graphQLType.StaffInputArg))
    ),
    Field(
      name = "createProduct",
      fieldType = graphQLType.ProductType,
      arguments = graphQLType.ProductInputArg :: Nil,
      resolve = sangriaContext => productResolver.createProduct(sangriaContext.ctx, sangriaContext.arg(graphQLType.ProductInputArg))
    ),
    //category
    Field(
      name = "createCategory",
      fieldType = graphQLType.CategoryType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => categoryResolver.category(sangriaContext.ctx, new Category(name = sangriaContext.arg[String]("name")))
    ),
    Field(
      name = "deleteCategory",
      fieldType = schema.IntType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => categoryResolver.deleteCategory(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    //productStock
    Field(
      name = "createProductStock",
      fieldType = graphQLType.ProductStockType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => productStockResolver.createProductStock(sangriaContext.ctx, new ProductStock(name = sangriaContext.arg[String]("name")))
    ),
    Field(
      name = "deleteProductStock",
      fieldType = schema.IntType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productStockResolver.deleteProductStock(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    //product
    Field(
      name = "updateProduct",
      fieldType = OptionType(graphQLType.ProductType),
      arguments = List(
        Argument("productId", schema.StringType),
        Argument("categoryId", schema.StringType),
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => productResolver.updateProduct(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("productId")), UUID.fromString(sangriaContext.arg[String]("categoryId")), sangriaContext.arg[String]("name"))
    ),
    Field(
      name = "deleteProduct",
      fieldType = schema.BooleanType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productResolver.deleteProduct(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    //productDetail
    Field(
      name = "deleteProductDetail",
      fieldType = schema.BooleanType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productDetailResolver.deleteProductDetail(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    Field(
      name = "createProductDetail",
      fieldType = graphQLType.ProductDetailType,
      arguments = graphQLType.ProductDetailInputArg :: Nil,
      resolve = sangriaContext => productDetailResolver.addProductDetail(sangriaContext.ctx, sangriaContext.arg(graphQLType.ProductDetailInputArg))
    ),
    Field(
      name = "productDetail",
      fieldType = OptionType(graphQLType.ProductDetailType),
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productDetailResolver.productDetail(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    //transaction
    Field(
      name = "createTransaction",
      fieldType = graphQLType.CreateTransactionResultType,
      resolve = sangriaContext => transactionResolver.createTransaction(sangriaContext.ctx)
    ),
    Field(
      name = "checkout",
      fieldType = graphQLType.TransactionResultType,
      arguments = graphQLType.TransactionInputArg :: Nil,
      resolve = sangriaContext => transactionResolver.createTransactionDetail(sangriaContext.ctx, sangriaContext.arg(graphQLType.TransactionInputArg))
    ),
    Field(
      name = "completePayment",
      fieldType = graphQLType.TransactionResultType,
      arguments = List(
        Argument("transactionId", schema.StringType)
      ),
      resolve = sangriaContext => transactionResolver.completePayment(sangriaContext.ctx, sangriaContext.arg[String]("transactionId"))
    ),
    Field(
      name = "updateStaffTransaction",
      fieldType = OptionType(CustomScalar.UUIDType),
      arguments = List(
        Argument("transactionId", schema.StringType),
        Argument("staffId", schema.StringType)
      ),
      resolve = sangriaContext => transactionResolver.updateStaff(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("transactionId")), UUID.fromString(sangriaContext.arg[String]("staffId")))
    ),
    Field(
      name = "updateCustomerTransaction",
      fieldType = OptionType(CustomScalar.UUIDType),
      arguments = List(
        Argument("transactionId", schema.StringType),
        Argument("customerId", schema.StringType)
      ),
      resolve = sangriaContext => transactionResolver.updateCustomer(sangriaContext.ctx, UUID.fromString(sangriaContext.arg[String]("transactionId")), UUID.fromString(sangriaContext.arg[String]("customerId")))
    )
  )

}
