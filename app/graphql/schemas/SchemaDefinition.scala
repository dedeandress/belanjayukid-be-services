package graphql.schemas

import com.google.inject.Inject
import graphql.GraphQLType
import graphql.resolvers.{CategoryResolver, ProductResolver, RoleResolver, StaffResolver, UserProfileResolver, UserResolver}
import sangria.schema.{Field, OptionType}


class SchemaDefinition @Inject()(staffResolver: StaffResolver
                                 , userResolver: UserResolver, userProfileResolver: UserProfileResolver
                                 , roleResolver: RoleResolver, categoryResolver: CategoryResolver
                                 , productResolver: ProductResolver, graphQLType: GraphQLType){

//  val Queries: List[Field[Unit, Unit]] = List(
//    Field(
//      name = "",
//      fieldType = StaffType,
//      resolve =
//    )
//  )

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
    )
  )

}
