package repositories.repositoryInterfaces

import java.util.UUID

import models.PurchasesTransaction

import scala.concurrent.Future

trait PurchasesTransactionRepository {

  def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): Future[UUID]

}
