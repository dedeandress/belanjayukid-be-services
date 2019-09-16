package repositories.repositoryInterfaces

import java.util.UUID

import models.TransactionDetail

import scala.concurrent.Future

trait TransactionDetailRepository {

  def addTransactionDetail(details: List[TransactionDetail]): Future[Int]

  def updateTransactionDetailStatus(transactionDetailId: UUID, status: Int)

  def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]]

}
