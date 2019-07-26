package graphql.schemas

import com.google.inject.Inject
import graphql.GraphQLType
import graphql.resolvers.{CategoryResolver, ProductResolver, RoleResolver, StaffResolver, UserProfileResolver, UserResolver}
import models.Category
import sangria.schema
import sangria.schema.{Argument, Field, ListType, OptionType}


class SchemaDefinition @Inject()(staffResolver: StaffResolver
                                 , userResolver: UserResolver, userProfileResolver: UserProfileResolver
                                 , roleResolver: RoleResolver, categoryResolver: CategoryResolver
                                 , productResolver: ProductResolver, graphQLType: GraphQLType){

  val Queries: List[Field[Unit, Unit]] = List(
    Field(
      name = "getAllCategory",
      fieldType = ListType(graphQLType.CategoryType),
      resolve = _ => categoryResolver.getAllCategory
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
    )
  )

}
