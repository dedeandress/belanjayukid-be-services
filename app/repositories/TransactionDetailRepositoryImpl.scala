package repositories

import java.util.UUID

import com.google.inject.Inject
import models.TransactionDetail
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionDetailRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class TransactionDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends TransactionDetailRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addTransactionDetail(details: List[TransactionDetail]): Future[Int] = {
    play.Logger.warn("add transactionDetail")
    val insert = for(detail <- details) yield {
      QueryUtility.transactionDetailQuery += detail
    }
    db.run(DBIO.seq(insert: _*))
    Future.successful(1)
  }

  override def updateTransactionDetailStatus(transactionDetailId: UUID, status: Int): Unit = {
    play.Logger.warn("update transactionDetail status")
    db.run(Action.updateTransaction(transactionDetailId, status))
  }

  override def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]] = {
    play.Logger.warn("find transactionDetail")
    db.run(Action.findTransactionDetailByTransactionId(transactionId))
  }

  object Action {

    def addTransactionDetail(transactionDetail: TransactionDetail): DBIO[UUID] = for {
      id <- QueryUtility.transactionDetailQuery returning QueryUtility.transactionDetailQuery.map(_.id) += transactionDetail
    }yield id

    def findTransactionDetailByTransactionId(transactionId: UUID): DBIO[Seq[TransactionDetail]] = for {
      details <- QueryUtility.transactionDetailQuery.filter(_.transactionId === transactionId).result
    }yield details

    def updateTransaction(transactionDetailId: UUID, status: Int): DBIO[Int] = for{
      update <- QueryUtility.transactionDetailQuery.filter(_.id === transactionDetailId).map(_.status).update(status)
    }yield update
  }

}
