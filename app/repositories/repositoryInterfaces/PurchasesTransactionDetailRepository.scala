package repositories.repositoryInterfaces

import java.util.UUID

import models.PurchasesTransactionDetail

import scala.concurrent.Future

trait PurchasesTransactionDetailRepository {

  def addPurchasesTransactionDetails(purchasesTransactionId: UUID, purchasesTransactionDetails: Seq[PurchasesTransactionDetail]): Future[Seq[PurchasesTransactionDetail]]

  def findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId: UUID): Future[Seq[PurchasesTransactionDetail]]

}
