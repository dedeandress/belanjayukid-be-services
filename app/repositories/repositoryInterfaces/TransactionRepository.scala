package repositories.repositoryInterfaces

import java.util.UUID

import models.Transaction

import scala.concurrent.Future

trait TransactionRepository {

  def addTransaction(transaction: Transaction): Future[UUID]

  def updateTransactionStatus(transactionId: UUID, status: Int): Future[Option[Int]]

  def getTransactionStatus(transactionId: UUID): Future[Option[Int]]

}
