package graphql.schemas

import com.google.inject.Inject
import graphql.{Context, GraphQLType}
import graphql.resolvers.{CategoryResolver, ProductResolver, ProductStockResolver, RoleResolver, StaffResolver, UserProfileResolver, UserResolver}
import models.{Category, ProductStock}
import sangria.schema
import sangria.schema.{Argument, Field, InterfaceType, ListType, OptionType}


class SchemaDefinition @Inject()(staffResolver: StaffResolver
                                 , userResolver: UserResolver, userProfileResolver: UserProfileResolver
                                 , roleResolver: RoleResolver, categoryResolver: CategoryResolver
                                 , productResolver: ProductResolver, productStockResolver: ProductStockResolver
                                 , graphQLType: GraphQLType){

  val Queries: List[Field[Context, Unit]] = List(
    Field(
      name = "categories",
      fieldType = ListType(graphQLType.CategoryType),
      resolve = _ => categoryResolver.categories
    ),
    Field(
      name = "productStocks",
      fieldType = ListType(graphQLType.ProductStockType),
      resolve = _ => productStockResolver.productStocks
    ),
    Field(
      name = "roles",
      fieldType = ListType(graphQLType.RoleType),
      resolve = _ => staffResolver.roles
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
      resolve = sangriaContext => staffResolver.createStaff(sangriaContext.arg(graphQLType.StaffInputArg))
    ),
    Field(
      name = "createProduct",
      fieldType = graphQLType.ProductType,
      arguments = graphQLType.ProductInputArg :: Nil,
      resolve = sangriaContext => productResolver.createProduct(sangriaContext.arg(graphQLType.ProductInputArg))
    ),
    Field(
      name = "createCategory",
      fieldType = graphQLType.CategoryType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => categoryResolver.category(new Category(name = sangriaContext.arg[String]("name")))
    ),
    Field(
      name = "createProductStock",
      fieldType = graphQLType.ProductStockType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => productStockResolver.createProductStock(new ProductStock(name = sangriaContext.arg[String]("name")))
    )
  )

}
