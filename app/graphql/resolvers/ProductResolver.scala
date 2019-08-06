package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductInput
import models.Products
import services.ProductService

import scala.concurrent.Future

class ProductResolver @Inject()(productService: ProductService){

  def product(id: UUID): Future[Option[Products]] = productService.findProduct(id)

  def createProduct(productInput: ProductInput): Future[Products] = productService.addProduct(productInput)

}
