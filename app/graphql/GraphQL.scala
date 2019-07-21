package graphql

import com.google.inject.Inject
import graphql.schemas.UserSchema

class GraphQL @Inject()(userSchema: UserSchema){

  import sangria.schema.{ObjectType, fields}

  val Schema = sangria.schema.Schema(
    query = ObjectType("Query",
      fields(
        userSchema.Queries: _*
      )
    ),

    mutation = Some(ObjectType("Mutation",
      fields(
        userSchema.Mutations: _*
      ))
    )
  )

}
