package services

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import models.ProductStock
import repositories.repositoryInterfaces.ProductStockRepository
import utilities.JWTUtility

import scala.concurrent.Future

class ProductStockService @Inject()(productStockRepository: ProductStockRepository){

  def findProductStock(context: Context, id: UUID): Future[Option[ProductStock]] = {
    JWTUtility.isAdmin(context)
    productStockRepository.findProductStock(id)
  }

  def getAllProductStock(context: Context): Future[Seq[ProductStock]] = {
    JWTUtility.isAdmin(context)
    productStockRepository.getAllProductStock
  }

  def createProductStock(context: Context, productStock: ProductStock): Future[ProductStock] ={
    JWTUtility.isAdmin(context)
    productStockRepository.addProductStock(productStock)
  }

}
