package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.ProductStock
import repositories.repositoryInterfaces.ProductStockRepository
import services.ProductStockService

import scala.concurrent.Future

class ProductStockResolver @Inject()(productStockService: ProductStockService){

  def productStock(id: UUID): Future[Option[ProductStock]] = productStockService.findProductStock(id)

  def productStocks: Future[Seq[ProductStock]] = productStockService.getAllProductStock
  
  def createProductStock(productStock: ProductStock): Future[ProductStock] = productStockService.createProductStock(productStock)

}
