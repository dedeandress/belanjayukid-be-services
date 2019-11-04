package repositories.repositoryInterfaces

import java.util.UUID

import models.Supplier

import scala.concurrent.Future

trait SupplierRepository {

  def addSupplier(supplier: Supplier): Future[Option[Supplier]]

  def deleteSupplier(supplierId: UUID): Future[Int]

  def updateSupplier(supplier: Supplier): Future[Option[Supplier]]

  def findById(id: UUID): Future[Option[Supplier]]

  def findAll(): Future[Seq[Supplier]]

}
