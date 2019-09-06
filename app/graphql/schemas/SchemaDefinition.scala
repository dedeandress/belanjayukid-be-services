package graphql.schemas

import java.util.UUID

import com.google.inject.Inject
import graphql.GraphQLType
import graphql.resolvers.{CategoryResolver, ProductDetailResolver, ProductResolver, ProductStockResolver, RoleResolver, StaffResolver, UserProfileResolver, UserResolver}
import models.{Category, ProductStock}
import sangria.schema
import sangria.schema.{Argument, Field, ListType, OptionType}


class SchemaDefinition @Inject()(staffResolver: StaffResolver
                                 , userResolver: UserResolver, userProfileResolver: UserProfileResolver
                                 , roleResolver: RoleResolver, categoryResolver: CategoryResolver
                                 , productResolver: ProductResolver, productStockResolver: ProductStockResolver
                                 , productDetailResolver: ProductDetailResolver
                                 , graphQLType: GraphQLType){

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "getAllCategory",
      fieldType = ListType(graphQLType.CategoryType),
      resolve = _ => categoryResolver.getAllCategory
    ),
    Field(
      name = "getAllProductStock",
      fieldType = ListType(graphQLType.ProductStockType),
      resolve = _ => productStockResolver.getAllProductStock
    ),
    Field(
      name = "getProductById",
      fieldType = OptionType(graphQLType.ProductType),
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productResolver.findProduct(UUID.fromString(sangriaContext.arg[String]("id")))
    )
  )

  val Mutations: List[Field[Unit, Unit]] = List(
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
      resolve = sangriaContext => productResolver.updateProduct(UUID.fromString(sangriaContext.arg[String]("productId")), UUID.fromString(sangriaContext.arg[String]("categoryId")), sangriaContext.arg[String]("name"))
    ),
    //productDetail
    Field(
      name = "deleteProductDetail",
      fieldType = schema.BooleanType,
      arguments = List(
        Argument("id", schema.StringType)
      ),
      resolve = sangriaContext => productDetailResolver.deleteProductDetail(UUID.fromString(sangriaContext.arg[String]("id")))
    ),
    Field(
      name = "addProductDetail",
      fieldType = graphQLType.ProductDetailType,
      arguments = graphQLType.ProductDetailInputArg :: Nil,
      resolve = sangriaContext => productDetailResolver.addProductDetail(sangriaContext.arg(graphQLType.ProductDetailInputArg))
    )
  )

}
