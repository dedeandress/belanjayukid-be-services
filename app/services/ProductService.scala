package services

import java.util.UUID

import com.google.inject.Inject
import errors.{AuthorizationException, BadRequest, NotFound}
import graphql.Context
import graphql.`type`.ProductsResult
import graphql.input.ProductInput
import models.Products
import repositories.repositoryInterfaces.{CategoryRepository, ProductDetailRepository, ProductStockRepository, ProductsRepository}
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class ProductService @Inject()(productsRepository: ProductsRepository, categoryRepository: CategoryRepository, productDetailRepository: ProductDetailRepository, productStockRepository: ProductStockRepository, implicit val executionContext: ExecutionContext) {

  def findProductByID(context: Context, id: String): Future[Option[Products]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    try {
      val productId = UUID.fromString(id)
      productsRepository.findProduct(productId)
    }catch {
      case _ : IllegalArgumentException => throw BadRequest("Product ID is not valid")
    }
  }

  def addProduct(context: Context, productInput: ProductInput): Future[Products] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    try {
      val categoryId = UUID.fromString(productInput.categoryId)
      for {
        product <- productsRepository.addProduct(new Products(SKU = productInput.SKU, name = productInput.name, imageUrl = productInput.imageUrl, categoryId = categoryId))
        _ <- productDetailRepository.addProductDetail(product.id, productInput)
      } yield product
    }catch {
        case _ : IllegalArgumentException => throw BadRequest("Category ID is not valid")
    }
  }

  def updateProduct(context: Context, productId: String, categoryId: String, name: String): Future[Option[Products]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    try {
      productsRepository.findProduct(UUID.fromString(productId)).flatMap {
        case Some(product) =>
          Try(productsRepository.updateProduct(product.copy(categoryId = UUID.fromString(categoryId), name = name))) match {
            case Success(productResult) => productResult
            case Failure(_: IllegalArgumentException) => throw BadRequest("Category ID is not valid")
          }
        case None => throw NotFound("Product Not Found")
      }
    } catch {
      case _ : IllegalArgumentException => throw BadRequest("Product ID is not valid")
    }
  }

  def deleteProduct(context: Context, id: String): Future[Boolean] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    try {
      productsRepository.deleteProduct(UUID.fromString(id)).map {
        case 0 => false
        case _ => true
      }
    }catch {
      case _ : IllegalArgumentException => throw BadRequest("Product ID is not valid")
    }
  }

  def findProduct(context: Context, name: String): Future[Seq[Products]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    productsRepository.findProduct(name)
  }

  def getAllProductsWithPagination(context: Context, limit: Int): Future[ProductsResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    productsRepository.getAllProductsWithPagination(limit)
  }

  def getAllProducts(context: Context): Future[Seq[Products]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    productsRepository.getAllProducts
  }


}
