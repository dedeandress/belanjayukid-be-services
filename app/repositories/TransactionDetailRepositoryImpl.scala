package repositories

import java.util.UUID

import com.google.inject.Inject
import models.TransactionDetail
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionDetailRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class TransactionDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends TransactionDetailRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addTransactionDetails(details: List[TransactionDetail]): Future[Int] = {
    play.Logger.warn("add transactionDetail")
    val insert = for (detail <- details) yield {
      QueryUtility.transactionDetailQuery += detail
    }
    db.run(DBIO.seq(insert: _*))
    Future.successful(1)
  }

  override def addTransactionDetail(detail: TransactionDetail): Future[Int] = db.run(Action.addTransactionDetail(detail))

  override def updateTransactionDetailStatus(transactionDetailId: UUID, status: Int): Future[Option[Int]] = {
    play.Logger.warn("update transactionDetail status")
    db.run(Action.updateTransaction(transactionDetailId, status))
  }

  override def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]] = {
    play.Logger.warn("find transactionDetail")
    db.run(Action.findTransactionDetailByTransactionId(transactionId))
  }

  object Action {

    def addTransactionDetail(transactionDetail: TransactionDetail): DBIO[Int] = for {
      id <- QueryUtility.transactionDetailQuery returning QueryUtility.transactionDetailQuery.map(_.status) += transactionDetail
    } yield id

    def findTransactionDetailByTransactionId(transactionId: UUID): DBIO[Seq[TransactionDetail]] = for {
      details <- QueryUtility.transactionDetailQuery.filter(_.transactionId === transactionId).result
    } yield details

    def updateTransaction(transactionDetailId: UUID, status: Int): DBIO[Option[Int]] = for {
      update <- QueryUtility.transactionDetailQuery.filter(_.id === transactionDetailId).map(_.status).update(status)
      status <- QueryUtility.transactionDetailQuery.filter(_.id === transactionDetailId).map(_.status).result.headOption
    } yield status

    def getTransactionDetailStatus(transactionId: UUID): DBIO[Option[Int]] = for {
      details <- QueryUtility.transactionDetailQuery.filter(_.id === transactionId).map(_.status).result.headOption
    } yield details
  }

}
