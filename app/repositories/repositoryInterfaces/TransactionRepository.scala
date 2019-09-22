package repositories.repositoryInterfaces

import java.util.UUID

import models.Transaction

import scala.concurrent.Future

trait TransactionRepository {

  def addTransaction(transaction: Transaction): Future[UUID]

  def updateTransactionStatus(transactionId: UUID, status: Int): Future[Option[Int]]

  def getTransactionStatus(transactionId: UUID): Future[Option[Int]]

  def getTransactions(status: Int): Future[Seq[Transaction]]

  def getTransaction(transactionId: UUID): Future[Option[Transaction]]

  def updateStaff(transactionId: UUID, staffId: UUID): Future[Option[UUID]]

  def updateCustomer(transactionId: UUID, customerId: UUID): Future[Option[UUID]]

  def updateTransaction(transactionId: UUID, status: Int, staffId: UUID): Future[Option[Int]]

}
