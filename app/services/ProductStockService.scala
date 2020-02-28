package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import models.ProductStock
import repositories.repositoryInterfaces.ProductStockRepository
import utilities.JWTUtility

import scala.concurrent.Future

class ProductStockService @Inject()(productStockRepository: ProductStockRepository) {

  def findProductStock(context: Context, id: UUID): Future[Option[ProductStock]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productStockRepository.findProductStock(id)
  }

  def getAllProductStock(context: Context): Future[Seq[ProductStock]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productStockRepository.getAllProductStock
  }

  def createProductStock(context: Context, productStock: ProductStock): Future[ProductStock] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productStockRepository.addProductStock(productStock)
  }

  def deleteProductStock(context: Context, id: UUID): Future[Int] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productStockRepository.deleteProductStock(id)
  }

}
