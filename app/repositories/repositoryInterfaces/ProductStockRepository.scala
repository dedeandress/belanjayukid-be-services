package repositories.repositoryInterfaces

import java.util.UUID

import models.ProductStock

import scala.concurrent.Future

trait ProductStockRepository {

  def findProductStock(id: UUID): Future[Option[ProductStock]]

  def addProductStock(productStock: ProductStock): Future[ProductStock]

  def getAllProductStock: Future[Seq[ProductStock]]

}
