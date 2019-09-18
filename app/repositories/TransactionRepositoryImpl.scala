package repositories

import java.util.UUID

import javax.inject.Inject
import models.Transaction
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class TransactionRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends TransactionRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addTransaction(transaction: Transaction): Future[UUID] = {
    play.Logger.warn(s"get TransactionStatus : $transaction")
    db.run(Action.addTransaction(transaction))
  }

  override def updateTransactionStatus(transactionId: UUID, status: Int): Future[Option[Int]] = {
    play.Logger.warn(s"update TransactionStatus : $transactionId and status: $status")
    db.run(Action.updateTransaction(transactionId, status))
  }

  override def getTransactionStatus(transactionId: UUID): Future[Option[Int]] = {
    play.Logger.warn(s"get TransactionStatus : $transactionId")
    db.run(Action.getTransactionStatus(transactionId))
  }

  override def getTransactions(status: Int): Future[Seq[Transaction]] = {
    play.Logger.warn(s"get Transactions with status : $status")
    db.run(Action.getAllTransaction(status))
  }

  override def getTransaction(transactionId: UUID): Future[Option[Transaction]] = {
    play.Logger.warn(s"get Transaction : $transactionId")
    db.run(Action.getTransaction(transactionId))
  }

  object Action {

    def addTransaction(transaction: Transaction) : DBIO[UUID] = for {
      id <- QueryUtility.transactionsQuery returning  QueryUtility.transactionsQuery.map(_.id) += transaction
    }yield id

    def updateTransaction(transactionId: UUID, status: Int): DBIO[Option[Int]] = for{
      _ <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).update(status)
      transactionStatus <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).result.headOption
    }yield transactionStatus

    def getTransactionStatus(transactionId: UUID): DBIO[Option[Int]] = for{
      status <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).result.headOption
    }yield status

    def getAllTransaction(status: Int): DBIO[Seq[Transaction]] = for {
      transactions <- QueryUtility.transactionsQuery.filter(_.status === status).result
    }yield transactions

    def getTransaction(transactionId: UUID) : DBIO[Option[Transaction]] = for{
      transaction <- QueryUtility.transactionsQuery.filter(_.id === transactionId).result.headOption
    }yield transaction
  }
}
