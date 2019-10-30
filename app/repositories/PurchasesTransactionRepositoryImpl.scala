package repositories

import java.util.UUID

import com.google.inject.Inject
import models.PurchasesTransaction
import modules.AppDatabase
import repositories.repositoryInterfaces.PurchasesTransactionRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends PurchasesTransactionRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): Future[UUID] = db.run(Actions.addPurchasesTransaction(purchasesTransaction))

  object Actions {

    def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): DBIO[UUID] = for{
      id <- QueryUtility.purchasesTransactionQuery returning QueryUtility.purchasesTransactionQuery.map(_.id) += purchasesTransaction
    }yield id

    def getPurchasesTransactionStatus(transactionId: UUID): DBIO[Option[Int]] = for {
      status <- QueryUtility.purchasesTransactionQuery.filter(_.id === transactionId).map(_.status).result.headOption
    } yield status

    def getAllPurchasesTransaction(status: Int): DBIO[Seq[PurchasesTransaction]] = for {
      purchasesTransactions <- QueryUtility.purchasesTransactionQuery.filter(_.status === status).result
    } yield purchasesTransactions

    def getPurchasesTransaction(purchasesTransactionId: UUID): DBIO[Option[PurchasesTransaction]] = for {
      purchasesTransaction <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).result.headOption
    } yield purchasesTransaction

  }

}
