package repositories

import java.util.UUID

import com.google.inject.Inject
import models.{PurchasesTransactionDetail, TransactionDetail}
import modules.AppDatabase
import repositories.repositoryInterfaces.PurchasesTransactionDetailRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends PurchasesTransactionDetailRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  private def purchasesTransactionDetailTableWithObject =
    (QueryUtility.purchasesTransactionDetailQuery returning QueryUtility.purchasesTransactionDetailQuery.map(_.id)).into((transactionDetail, id) => transactionDetail.copy(id = id))

  override def addPurchasesTransactionDetails(purchasesTransactionId: UUID, purchasesTransactionDetails: Seq[PurchasesTransactionDetail]): Future[Seq[PurchasesTransactionDetail]] = {
    play.Logger.warn(s"add PurchasesTransactionDetail with transactionId: $purchasesTransactionId")
    play.Logger.warn(s"transactionDetails : ${purchasesTransactionDetails.toString()}")
    db.run{ purchasesTransactionDetailTableWithObject ++= purchasesTransactionDetails }
  }

  override def findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId: UUID): Future[Seq[PurchasesTransactionDetail]] = {
    play.Logger.warn(s"find PurchasesTransactionDetail with id: $purchasesTransactionId")
    db.run(Actions.findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId))
  }

  object Actions{

    def findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId: UUID): DBIO[Seq[PurchasesTransactionDetail]] = for {
      details <- QueryUtility.purchasesTransactionDetailQuery.filter(_.purchasesTransactionId === purchasesTransactionId).result
    } yield details

  }

}
