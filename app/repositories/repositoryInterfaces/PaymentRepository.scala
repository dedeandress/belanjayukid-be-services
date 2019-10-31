package repositories.repositoryInterfaces

import models.Payment

import scala.concurrent.Future

trait PaymentRepository {

  def addPayment(payment: Payment): Future[BigDecimal]

}
