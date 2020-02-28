package repositories

import java.util.UUID

import com.google.inject.Inject
import errors.NotFound
import models.Payment
import modules.AppDatabase
import repositories.repositoryInterfaces.PaymentRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class PaymentRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends PaymentRepository{
  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addPayment(payment: Payment): Future[UUID] = db.run(Actions.addPayment(payment))

  override def updatePayment(transactionId: UUID, debt: BigDecimal, amountOfPayment: BigDecimal, paymentStatus: Int): Future[BigDecimal] = {
    db.run(QueryUtility.transactionsQuery.filter(_.id === transactionId).result.headOption).flatMap{
      transaction =>
        db.run(QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.paymentStatus).update(paymentStatus)).flatMap{
          _ =>
            db.run(Actions.updatePayment(transaction.get.paymentId, debt, amountOfPayment))
        }
    }
  }

  override def updatePaymentPurchases(purchasesTransactionId: UUID, debt: BigDecimal, amountOfPayment: BigDecimal, paymentStatus: Int): Future[BigDecimal] = {
    db.run(QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).result.headOption).flatMap{
      transaction =>
        db.run(QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.paymentStatus).update(paymentStatus)).flatMap{
          _ =>
            db.run(Actions.updatePayment(transaction.get.paymentId, debt, amountOfPayment))
        }
    }
  }

  override def findById(id: UUID): Future[Option[Payment]] = {
    db.run(Actions.getPayment(id))
  }

  object Actions {

    def getPayment(id: UUID): DBIO[Option[Payment]] = for{
      payment <- QueryUtility.paymentQuery.filter(_.id === id).result.headOption
    }yield payment

    def addPayment(payment: Payment): DBIO[UUID] = for {
      id <- QueryUtility.paymentQuery returning QueryUtility.paymentQuery.map(_.id) += payment
    } yield id

    def updatePayment(paymentId: UUID, debt: BigDecimal, amountOfPayment: BigDecimal): DBIO[BigDecimal] = for {
      updatePayment <- QueryUtility.paymentQuery.filter(_.id === paymentId).map(payment => (payment.debt, payment.amountOfPayment)).update(debt, amountOfPayment)
      getPayment <- getPayment(paymentId)
      result <- updatePayment match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(getPayment.get.debt)
      }
    }yield result

  }

}
