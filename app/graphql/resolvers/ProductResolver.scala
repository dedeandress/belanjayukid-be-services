package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import graphql.`type`.ProductsResult
import graphql.input.ProductInput
import models.Products
import services.ProductService

import scala.concurrent.Future

class ProductResolver @Inject()(productService: ProductService){

  def product(context: Context, id: String): Future[Option[Products]] = productService.findProductByID(context, id)

  def createProduct(context: Context, productInput: ProductInput): Future[Products] = productService.addProduct(context, productInput)

  def updateProduct(context: Context, productId: String, categoryId: String, name: String): Future[Option[Products]] = productService.updateProduct(context, productId, categoryId, name)

  def deleteProduct(context: Context, productId: String): Future[Boolean] = productService.deleteProduct(context, productId)

  def products(context: Context, name: String): Future[Seq[Products]] = productService.findProduct(context, name)

  def products(context: Context, limit: Int): Future[ProductsResult] = productService.getAllProductsWithPagination(context, limit)

  def products(context: Context): Future[Seq[Products]] = productService.getAllProducts(context)

}
