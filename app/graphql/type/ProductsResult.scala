package graphql.`type`

import models.Products

case class ProductsResult(products: Seq[Products], totalCount: Int, hasNextData: Boolean)
