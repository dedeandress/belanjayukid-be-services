package repositories

import com.google.inject.Inject
import models.Payment
import modules.AppDatabase
import repositories.repositoryInterfaces.PaymentRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class PaymentRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends PaymentRepository{
  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addPayment(payment: Payment): Future[BigDecimal] = db.run(Actions.addPayment(payment))

  object Actions {

    def addPayment(payment: Payment): DBIO[BigDecimal] = for {
      debt <- QueryUtility.paymentQuery returning QueryUtility.paymentQuery.map(_.debt) += payment
    }yield debt

  }

}
