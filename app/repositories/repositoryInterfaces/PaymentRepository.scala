package repositories.repositoryInterfaces

import java.util.UUID

import models.Payment

import scala.concurrent.Future

trait PaymentRepository {

  def addPayment(payment: Payment): Future[UUID]

  def updatePayment(transactionId: UUID, debt: BigDecimal, amountOfPayment: BigDecimal, paymentStatus: Int): Future[BigDecimal]

  def findById(id: UUID): Future[Option[Payment]]

  def updatePaymentPurchases(purchasesTransactionId: UUID, debt: BigDecimal, amountOfPayment: BigDecimal, paymentStatus : Int): Future[BigDecimal]

}
