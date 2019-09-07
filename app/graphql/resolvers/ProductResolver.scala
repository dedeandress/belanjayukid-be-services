package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import graphql.input.ProductInput
import models.Products
import services.ProductService

import scala.concurrent.Future

class ProductResolver @Inject()(productService: ProductService){

  def product(id: UUID): Future[Option[Products]] = productService.findProduct(id)

  def createProduct(context: Context, productInput: ProductInput): Future[Products] = productService.addProduct(context, productInput)

  def updateProduct(context: Context, productId: UUID, categoryId: UUID, name: String): Future[Option[Products]] = productService.updateProduct(context, productId, categoryId, name)

  def deleteProduct(context: Context, productId: UUID): Future[Boolean] = productService.deleteProduct(context, productId)

}
