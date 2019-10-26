package repositories

import java.util.UUID

import graphql.`type`.TransactionsResult
import javax.inject.Inject
import models.Transaction
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionRepository
import utilities.{QueryUtility, TransactionStatus}

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

  override def updateStaff(transactionId: UUID, staffId: UUID): Future[Option[UUID]] = {
    play.Logger.warn(s"update staff: $staffId in Transaction : $transactionId")
    db.run(Action.updateStaff(transactionId, staffId))
  }

  override def updateCustomer(transactionId: UUID, customerId: UUID): Future[Option[UUID]] = {
    play.Logger.warn(s"update customer $customerId in Transaction : $transactionId")
    db.run(Action.updateCustomer(transactionId, customerId))
  }

  override def updateTransaction(transactionId: UUID, status: Int, staffId: UUID): Future[Option[Int]] = {
    db.run(Action.updateStaff(transactionId, staffId))
    db.run(Action.updateTransaction(transactionId, status))
  }

  override def getAllTransactionWithLimit(limit: Int): Future[TransactionsResult] = db.run(Action.getAllTransactionWithLimit(limit))

  override def updateTotalPrice(transactionId: UUID): Future[BigDecimal] = db.run(Action.updateTotalPrice(transactionId))

  object Action {

    def updateStaff(transactionId: UUID, staffId: UUID): DBIO[Option[UUID]] = for {
      _ <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.staffId).update(Some(staffId))
      staffId <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.staffId).result.headOption
    } yield staffId.get

    def updateCustomer(transactionId: UUID, customer: UUID): DBIO[Option[UUID]] = for {
      _ <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.customerId).update(Some(customer))
      customerId <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.customerId).result.headOption
    } yield customerId.get

    def addTransaction(transaction: Transaction): DBIO[UUID] = for {
      id <- QueryUtility.transactionsQuery returning QueryUtility.transactionsQuery.map(_.id) += transaction
    } yield id

    def updateTransaction(transactionId: UUID, status: Int): DBIO[Option[Int]] = for {
      _ <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).update(status)
      transactionStatus <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).result.headOption
    } yield transactionStatus

    def getTransactionStatus(transactionId: UUID): DBIO[Option[Int]] = for {
      status <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).result.headOption
    } yield status

    def getAllTransaction(status: Int): DBIO[Seq[Transaction]] = for {
      transactions <- QueryUtility.transactionsQuery.filter(_.status === status).result
    } yield transactions

    def getTransaction(transactionId: UUID): DBIO[Option[Transaction]] = for {
      transaction <- QueryUtility.transactionsQuery.filter(_.id === transactionId).result.headOption
    } yield transaction

    def getAllTransactionWithLimit(limit: Int): DBIO[TransactionsResult] = for {
      transactions <- QueryUtility.transactionsQuery.filter(_.status =!= TransactionStatus.INITIAL).sortBy(_.date.desc).take(limit).result
      numberOfProduct <- QueryUtility.transactionsQuery.filter(_.status =!= TransactionStatus.INITIAL).length.result
    } yield TransactionsResult(
      transactions = transactions,
      totalCount = numberOfProduct,
      hasNextData = numberOfProduct > limit
    )

    def updateTotalPrice(transactionId: UUID):DBIO[BigDecimal] = for {
      totalPrice <- QueryUtility.transactionDetailQuery.filter(_.transactionId === transactionId).map(_.subTotalPrice).sum.result
      _ <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.totalPrice).update(totalPrice.get)
    }yield totalPrice.get

  }

}
