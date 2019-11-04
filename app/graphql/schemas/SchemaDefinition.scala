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
                                 , customerResolver: CustomerResolver, purchasesTransactionResolver: PurchasesTransactionResolver
                                 , supplierResolver: SupplierResolver, graphQLType: GraphQLType) {

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
    Field(
      name = "transactionsWithLimit",
      fieldType = graphQLType.TransactionsResultType,
      arguments = List(
        Argument("limit", schema.IntType)
      ),
      resolve = sangriaContext => transactionResolver.getTransactionsWithLimit(sangriaContext.ctx, sangriaContext.arg[Int]("limit"))
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
    ),
    //customer
    Field(
      name = "customers",
      fieldType = ListType(graphQLType.CustomerType),
      resolve = sangriaContext => customerResolver.customers(sangriaContext.ctx)
    ),
    Field(
      name = "customer",
      fieldType = OptionType(graphQLType.CustomerType),
      arguments = List(
        Argument("customerId", schema.StringType)
      ),
      resolve = sangriaContext => customerResolver.customer(sangriaContext.ctx, sangriaContext.arg[String]("customerId"))
    ),
    //supplier
    Field(
      name = "suppliers",
      fieldType = ListType(graphQLType.SupplierType),
      resolve = sangriaContext => supplierResolver.suppliers(sangriaContext.ctx)
    ),
    Field(
      name = "supplier",
      fieldType = OptionType(graphQLType.SupplierType),
      arguments = List(
        Argument("supplierId", schema.StringType)
      ),
      resolve = sangriaContext => supplierResolver.supplier(sangriaContext.ctx, sangriaContext.arg[String]("supplierId"))
    )
  )

  val Mutations: List[Field[Context, Unit]] = List(
    Field(
      //staff
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
      name = "updateStaff",
      fieldType = OptionType(graphQLType.StaffType),
      arguments = List(
        Argument("staffId", schema.StringType),
        Argument("fullName", schema.StringType),
        Argument("phoneNumber", schema.StringType),
        Argument("address", schema.StringType),
        Argument("noNik", schema.StringType),
        Argument("dateOfBirth", schema.LongType),
        Argument("roleId", schema.StringType),
        Argument("staffEmail", schema.StringType)
      ),
      resolve = sangriaContext => staffResolver.updateStaff(
        sangriaContext.ctx, sangriaContext.arg[String]("staffId"),
        sangriaContext.arg[String]("fullName"), sangriaContext.arg[String]("phoneNumber"),
        sangriaContext.arg[String]("address"), sangriaContext.arg[String]("noNik"),
        sangriaContext.arg[Long]("dateOfBirth"), sangriaContext.arg[String]("roleId"),
        sangriaContext.arg[String]("staffEmail")
      )
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
        Argument("transactionId", schema.StringType),
        Argument("amountOfPayment", schema.BigDecimalType)
      ),
      resolve = sangriaContext => transactionResolver.completePayment(sangriaContext.ctx, sangriaContext.arg[String]("transactionId"), sangriaContext.arg[BigDecimal]("amountOfPayment"))
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
    ),
    //supplier
    Field(
      name = "createSupplier",
      fieldType = OptionType(graphQLType.SupplierType),
      arguments = List(
        Argument("name", schema.StringType),
        Argument("phoneNumber", schema.StringType),
        Argument("address", schema.StringType),
      ),
      resolve = sangriaContext => supplierResolver.createSupplier(sangriaContext.ctx, sangriaContext.arg[String]("name"), sangriaContext.arg[String]("phoneNumber"), sangriaContext.arg[String]("address"))
    ),
    Field(
      name = "updateSupplier",
      fieldType = OptionType(graphQLType.SupplierType),
      arguments = List(
        Argument("id", schema.StringType),
        Argument("name", schema.StringType),
        Argument("phoneNumber", schema.StringType),
        Argument("address", schema.StringType),
      ),
      resolve = sangriaContext => supplierResolver.updateSupplier(sangriaContext.ctx, sangriaContext.arg[String]("id"), sangriaContext.arg[String]("name"), sangriaContext.arg[String]("phoneNumber"), sangriaContext.arg[String]("address"))
    ),
    Field(
      name = "deleteSupplier",
      fieldType = schema.IntType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => supplierResolver.deleteSupplier(sangriaContext.ctx, sangriaContext.arg[String]("id"))
    ),
    //customer
    Field(
      name = "createCustomer",
      fieldType = OptionType(graphQLType.CustomerType),
      arguments = graphQLType.CustomerInputArg :: Nil,
      resolve = sangriaContext => customerResolver.addCustomer(sangriaContext.ctx, sangriaContext.arg(graphQLType.CustomerInputArg))
    ),
    Field(
      name = "updateCustomer",
      fieldType = OptionType(graphQLType.CustomerType),
      arguments = List(
        Argument("customerId", schema.StringType),
        Argument("fullName", schema.StringType),
        Argument("phoneNumber", schema.StringType),
        Argument("address", schema.StringType),
        Argument("noNik", schema.StringType),
        Argument("dateOfBirth", schema.LongType),
      ),
      resolve = sangriaContext => customerResolver.updateCustomer(sangriaContext.ctx, sangriaContext.arg[String]("customerId"),
        sangriaContext.arg[String]("fullName"), sangriaContext.arg[String]("phoneNumber"),
        sangriaContext.arg[String]("address"), sangriaContext.arg[String]("noNik"),
        sangriaContext.arg[Long]("dateOfBirth"))
    ),
    Field(
      name = "deleteCustomer",
      fieldType = schema.IntType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => customerResolver.deleteCustomer(sangriaContext.ctx, sangriaContext.arg[String]("id"))
    ),
    //purchases transaction
    Field(
      name = "createPurchasesTransaction",
      fieldType = graphQLType.CreatePurchasesTransactionResultType,
      resolve = sangriaContext => purchasesTransactionResolver.createPurchasesTransaction(sangriaContext.ctx)
    ),
    Field(
      name = "checkoutPurchases",
      fieldType = graphQLType.PurchasesTransactionResultType,
      arguments = graphQLType.PurchasesTransactionInputArg :: Nil,
      resolve = sangriaContext => purchasesTransactionResolver.checkout(sangriaContext.ctx, sangriaContext.arg(graphQLType.PurchasesTransactionInputArg))
    ),
    Field(
      name = "completePaymentPurchases",
      fieldType = graphQLType.PurchasesTransactionResultType,
      arguments = List(
        Argument("purchasesTransactionId", schema.StringType),
        Argument("amountOfPayment", schema.BigDecimalType)
      ),
      resolve = sangriaContext => purchasesTransactionResolver.completePayment(sangriaContext.ctx, sangriaContext.arg[String]("purchasesTransactionId"), sangriaContext.arg[BigDecimal]("amountOfPayment"))
    ),
    //checker
    Field(
      name = "checkTransaction",
      fieldType = OptionType(schema.IntType),
      arguments = graphQLType.CheckTransactionInputArg :: Nil,
      resolve = sangriaContext => transactionResolver.checkTransaction(sangriaContext.ctx, sangriaContext.arg(graphQLType.CheckTransactionInputArg))
    )
  )

}
