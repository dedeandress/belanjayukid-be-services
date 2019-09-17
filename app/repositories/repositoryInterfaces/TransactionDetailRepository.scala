package repositories.repositoryInterfaces

import java.util.UUID

import models.TransactionDetail

import scala.concurrent.Future

trait TransactionDetailRepository {

  def addTransactionDetails(details: List[TransactionDetail]): Future[Int]

  def updateTransactionDetailStatus(transactionDetailId: UUID, status: Int): Future[Option[Int]]

  def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]]

  def addTransactionDetail(detail: TransactionDetail): Future[Int]

}
