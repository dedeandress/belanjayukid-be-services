package graphql

import com.google.inject.Inject
import graphql.schemas.{SchemaDefinition, UserSchema}

class GraphQL @Inject()(userSchema: UserSchema, schemaDefinition: SchemaDefinition){

  import sangria.schema.{ObjectType, fields}
  /**
    * The constant that signifies the maximum allowed depth of query.
    */
  val maxQueryDepth = 15

  /**
    * The constant that signifies the maximum allowed complexity of query.
    */
  val maxQueryComplexity = 1000
  val Schema = sangria.schema.Schema(
    query = ObjectType("Query",
      fields(
        schemaDefinition.Queries: _*
      )
    ),

    mutation = Some(ObjectType("Mutation",
      fields(
        schemaDefinition.Mutations: _*
      )
    )
    )
  )

}
