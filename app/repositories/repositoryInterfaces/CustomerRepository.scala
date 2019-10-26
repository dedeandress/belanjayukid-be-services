package repositories.repositoryInterfaces

import java.util.UUID

import models.Customer

import scala.concurrent.Future

trait CustomerRepository {
  def addCustomer(customer: Customer): Future[Option[Customer]]

  def findById(id: UUID): Future[Option[Customer]]

  def findByUserId(userId: UUID): Future[Option[Customer]]

  def findAll(): Future[Seq[Customer]]

  def deleteCustomer(id: UUID): Future[Int]
}
