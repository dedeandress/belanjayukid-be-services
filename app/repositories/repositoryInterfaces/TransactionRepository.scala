package repositories.repositoryInterfaces

import java.util.UUID

import graphql.`type`.TransactionsResult
import models.{Transaction, TransactionDetail}

import scala.concurrent.Future

trait TransactionRepository {

  def addTransaction(transaction: Transaction): Future[UUID]

  def updateTransactionStatus(transactionId: UUID, status: Int): Future[Option[Int]]

  def getTransactionStatus(transactionId: UUID): Future[Option[Int]]

  def getTransactions(status: Int): Future[Seq[Transaction]]

  def getTransaction(transactionId: UUID): Future[Option[Transaction]]

  def updateStaff(transactionId: UUID, staffId: UUID): Future[Option[UUID]]

  def updateCustomer(transactionId: UUID, customerId: UUID): Future[Option[UUID]]

  def updateTransaction(transactionId: UUID, status: Int, staffId: UUID, customerId: UUID): Future[Option[Int]]

  def getAllTransactionWithLimit(limit: Int): Future[TransactionsResult]

  def updateTotalPrice(transactionId: UUID): Future[BigDecimal]

  def updateStock(transactionId: UUID): Future[Unit]

  def getTotalPriceAndDebt(transactionId: UUID): Future[(BigDecimal, BigDecimal)]

  def updatePaymentStatus(transactionId: UUID): Future[Int]

}
