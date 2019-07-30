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
      name = "getAllCategory",
      fieldType = ListType(graphQLType.CategoryType),
      resolve = _ => categoryResolver.getAllCategory
    ),
    Field(
      name = "getAllProductStock",
      fieldType = ListType(graphQLType.ProductStockType),
      resolve = _ => productStockResolver.getAllProductStock
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
      name = "addStaff",
      fieldType = OptionType(graphQLType.StaffType),
      arguments = graphQLType.StaffInputArg :: Nil,
      resolve = sangriaContext => staffResolver.addStaff(sangriaContext.arg(graphQLType.StaffInputArg))
    ),
    Field(
      name = "addProduct",
      fieldType = graphQLType.ProductType,
      arguments = graphQLType.ProductInputArg :: Nil,
      resolve = sangriaContext => productResolver.addProduct(sangriaContext.arg(graphQLType.ProductInputArg))
    ),
    Field(
      name = "addCategory",
      fieldType = graphQLType.CategoryType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => categoryResolver.addCategory(new Category(categoryName = sangriaContext.arg[String]("name")))
    ),
    Field(
      name = "addProductStock",
      fieldType = graphQLType.ProductStockType,
      arguments = List(
        Argument("name", schema.StringType)
      ),
      resolve = sangriaContext => productStockResolver.addProductStock(new ProductStock(name = sangriaContext.arg[String]("name")))
    )
  )

}
