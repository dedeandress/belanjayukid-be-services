package repositories

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

  override def addPayment(payment: Payment, paymentStatus: Int): Future[BigDecimal] = db.run(Actions.addPayment(payment, paymentStatus))

  object Actions {

    def addPayment(payment: Payment, paymentStatus: Int): DBIO[BigDecimal] = for {
      updatePaymentStatus <- QueryUtility.transactionsQuery.filter(_.id === payment.transactionId).map(_.paymentStatus).update(paymentStatus)
      debt <- QueryUtility.paymentQuery returning QueryUtility.paymentQuery.map(_.debt) += payment
      result <- updatePaymentStatus match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(debt)
      }
    }yield result

  }

}
