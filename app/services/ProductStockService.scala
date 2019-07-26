package services

import java.util.UUID

import com.google.inject.Inject
import models.ProductStock
import repositories.repositoryInterfaces.ProductStockRepository

import scala.concurrent.Future

class ProductStockService @Inject()(productStockRepository: ProductStockRepository){

  def findProductStock(id: UUID): Future[Option[ProductStock]] = productStockRepository.findProductStock(id)

}
