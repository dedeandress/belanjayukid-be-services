package repositories.repositoryInterfaces

import java.util.UUID

import models.TransactionDetail

import scala.concurrent.Future

trait TransactionDetailRepository {

  def addTransactionDetails(details: List[TransactionDetail], transactionId: UUID): Future[Seq[TransactionDetail]]

  def updateTransactionDetailStatus(transactionId: UUID): Future[Unit]

  def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]]

}
