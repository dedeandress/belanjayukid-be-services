package repositories.repositoryInterfaces

import java.util.UUID

import models.PurchasesTransaction

import scala.concurrent.Future

trait PurchasesTransactionRepository {

  def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): Future[(UUID, Int)]

  def getPurchasesTransactionStatus(id: UUID): Future[Option[Int]]

  def updatePurchasesTransaction(purchasesTransactionId: UUID, status: Int, staffId: UUID, supplierId: UUID): Future[Option[Int]]

  def updateTotalPrice(purchasesTransactionId: UUID): Future[BigDecimal]

  def updateStock(purchasesTransactionId: UUID): Future[Unit]

  def getTotalPriceAndDebt(purchasesTransactionId: UUID): Future[(BigDecimal, BigDecimal)]

  def updatePurchasesTransactionStatus(purchasesTransactionId: UUID, status: Int): Future[Option[Int]]

  def getTotalPrice(purchasesTransactionId: UUID): Future[Option[BigDecimal]]

}
