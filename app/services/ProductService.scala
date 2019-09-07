package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.input.ProductInput
import models.Products
import repositories.repositoryInterfaces.{CategoryRepository, ProductDetailRepository, ProductStockRepository, ProductsRepository}
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject()(productsRepository: ProductsRepository, categoryRepository: CategoryRepository, productDetailRepository: ProductDetailRepository, productStockRepository: ProductStockRepository, implicit val executionContext: ExecutionContext){

  def findProduct(id: UUID): Future[Option[Products]] = productsRepository.findProduct(id)

  def addProduct(context: Context, productInput: ProductInput): Future[Products] ={
    if(!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    for {
      product <- productsRepository.addProduct(new Products(SKU = productInput.SKU, name = productInput.name, categoryId = UUID.fromString(productInput.categoryId)))
      _ <- productDetailRepository.addProductDetail(product.id, productInput)
    }yield product
  }

  def updateProduct(context: Context, productId: UUID, categoryId: UUID, name: String): Future[Option[Products]] = {
    if(!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productsRepository.findProduct(productId).flatMap{
      product =>
        productsRepository.updateProduct(product.get.copy(categoryId = categoryId, name = name))
    }
  }

  def deleteProduct(context: Context, id: UUID): Future[Boolean] = {
    if(!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productsRepository.deleteProduct(id).map{
      case 0 => false
      case _ => true
    }
  }

  def findProduct(context: Context, name: String): Future[Seq[Products]] = {
    if(!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    productsRepository.findProduct(name)
  }


}
