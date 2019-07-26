package graphql

import com.google.inject.Inject
import graphql.schemas.{SchemaDefinition, UserSchema}

class GraphQL @Inject()(userSchema: UserSchema, schemaDefinition: SchemaDefinition){

  import sangria.schema.{ObjectType, fields}

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
