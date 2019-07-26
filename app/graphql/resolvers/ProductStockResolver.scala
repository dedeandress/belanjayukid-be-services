package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.ProductStock
import repositories.repositoryInterfaces.ProductStockRepository
import services.ProductStockService

import scala.concurrent.Future

class ProductStockResolver @Inject()(productStockService: ProductStockService){

  def findProductStock(id: UUID): Future[Option[ProductStock]] = productStockService.findProductStock(id)

  def addProductStock(productStock: ProductStock): Future[ProductStock] = productStockService.addProductStock(productStock)

}
