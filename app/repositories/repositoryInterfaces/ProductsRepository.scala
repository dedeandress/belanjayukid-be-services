package repositories.repositoryInterfaces

import java.util.UUID

import models.Products

import scala.concurrent.Future

trait ProductsRepository {

  def addProduct(product: Products): Future[Products]

  def findProduct(id: UUID): Future[Option[Products]]

  def updateProduct(id:UUID, products: Products): Future[Products]

}
