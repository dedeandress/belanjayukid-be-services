package utilities

import models.Products.ProductsTable
import slick.lifted.TableQuery

object TableQueryDB {
  val productQuery = TableQuery[ProductsTable]
}
