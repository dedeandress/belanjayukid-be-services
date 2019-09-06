package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductInput
import models.Products
import services.ProductService

import scala.concurrent.Future

class ProductResolver @Inject()(productService: ProductService){

  def findProduct(id: UUID): Future[Option[Products]] = productService.findProduct(id)

  def addProduct(productInput: ProductInput): Future[Products] = productService.addProduct(productInput)

  def updateProduct(productId: UUID, categoryId: UUID, name: String): Future[Option[Products]] = productService.updateProduct(productId, categoryId, name)

}
