package repositories.repositoryInterfaces

import java.util.UUID

import graphql.`type`.ProductsResult
import models.{Products, TransactionDetail}

import scala.concurrent.Future

trait ProductsRepository {

  def addProduct(product: Products): Future[Products]

  def findProduct(id: UUID): Future[Option[Products]]

  def updateProduct(product: Products): Future[Option[Products]]

  def deleteProduct(productsId: UUID): Future[Int]

  def findProduct(name: String): Future[Seq[Products]]

  def getAllProductsWithPagination(limit: Int): Future[ProductsResult]

  def getAllProducts: Future[Seq[Products]]

  def updateStock(productId: UUID, stock: Int): Future[Int]

  def decrementStock(details: Seq[TransactionDetail])

}
