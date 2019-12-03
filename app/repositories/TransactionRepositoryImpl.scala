package repositories

import java.util.UUID

import errors.NotFound
import graphql.`type`.TransactionsResult
import javax.inject.Inject
import models.Transaction
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionRepository
import utilities.{QueryUtility, TransactionDetailStatus, TransactionStatus}

import scala.concurrent.{ExecutionContext, Future}

class TransactionRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends TransactionRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addTransaction(transaction: Transaction): Future[UUID] = {
    play.Logger.warn(s"add Transaction : $transaction")
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
    play.Logger.warn(s"get Transactions with id : ${transactionId.toString}")
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

  override def updateTransaction(transactionId: UUID, status: Int, staffId: UUID, customerId: UUID): Future[Option[Int]] = {
    play.Logger.warn(s"update Transactions with status : $status, staffId: ${staffId.toString}, customerId: ${customerId.toString}")
    db.run(Action.updateTransaction(transactionId, status, staffId, customerId))
  }

  override def getAllTransactionWithLimit(limit: Int, status: Int): Future[TransactionsResult] = {
    play.Logger.warn(s"getAllTransactionWith Limit: $limit and status: $status")
    db.run(Action.getAllTransactionWithLimit(limit, status))
  }

  override def updateTotalPrice(transactionId: UUID, transactionDetailStatus: Int): Future[BigDecimal] = {
    db.run(sql"select purchase_price, selling_price, number_of_purchases from transaction_detail td join product_detail pd on td.product_detail_id = pd.id where td.transaction_id::varchar = ${transactionId.toString()} and td.status = ${transactionDetailStatus}".as[(BigDecimal, BigDecimal, Int)]).flatMap {
      list =>
        play.Logger.warn(list.toString())
        var profit: BigDecimal = 0
        var totalPrice: BigDecimal = 0
        for (item <- list) {
          profit += (item._3 * item._2) - (item._3 * item._1)
          totalPrice += item._3 * item._2
        }
        play.Logger.warn(s"Profit : $profit, totalPrice: $totalPrice")
        db.run(Action.updateProfitAndTotalPrice(transactionId, totalPrice, profit))
    }
  }

  override def updateStock(transactionId: UUID): Future[Unit] = {
    db.run(sql"select p.id::varchar, p.stock, td.number_of_purchases, pd.value from transaction_detail td join product_detail pd on td.product_detail_id = pd.id join products p on pd.product_id = p.id where ${transactionId.toString()} = td.transaction_id::varchar and td.status = ${TransactionDetailStatus.NOT_EMPTY}".as[(String, Int, Int, Int)]).flatMap {
      list =>
        play.Logger.warn(s"list of transactionDetail : ${list.toString()}")
        var stock = 0
        val update = for (item <- list) yield {
          stock = item._2 - (item._3 * item._4)
          play.Logger.warn(s"stock : $stock")
          QueryUtility.productQuery.filter(_.id === UUID.fromString(item._1)).map(_.stock).update(stock)
        }
        db.run(DBIO.seq(update: _*))
    }
  }

  override def getTotalPriceAndDebt(transactionId: UUID): Future[(BigDecimal, BigDecimal)] = {
    db.run(sql"select t.total_price, p.debt from transactions t join payments p on t.id = p.transaction_id where ${transactionId.toString()} = t.id::varchar".as[(BigDecimal, BigDecimal)]).map{
      result =>
        play.Logger.warn(s"result : ${result.toList.toString()}")
        (result.head._1, result.head._2)
    }
  }

  override def getTransactions(fromDate: Long, toDate: Long): Future[Seq[Transaction]] = db.run(Action.getTransaction(fromDate, toDate))

  override def updatePaymentStatus(transactionId: UUID, paymentStatus: Int): Future[Int] = db.run(Action.updatePaymentStatus(transactionId, paymentStatus))

  override def getTransactionsByPaymentStatus(paymentStatus: Int): Future[Seq[Transaction]] = db.run(Action.getTransactionByPaymentStatus(paymentStatus))

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

    def getAllTransactionWithLimit(limit: Int, status: Int): DBIO[TransactionsResult] = for {
      transactions <- QueryUtility.transactionsQuery.filter(_.status === status).sortBy(_.date.desc).take(limit).result
      numberOfProduct <- QueryUtility.transactionsQuery.filter(_.status === status).length.result
    } yield TransactionsResult(
      transactions = transactions,
      totalCount = numberOfProduct,
      hasNextData = numberOfProduct > limit
    )

    def updateProfitAndTotalPrice(transactionId: UUID, totalPrice: BigDecimal, profit: BigDecimal): DBIO[BigDecimal] = for {
      update <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(transaction => (transaction.totalPrice, transaction.profit)).update(totalPrice, profit)
      transaction <- getTransaction(transactionId)
      result <- update match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(transaction.get.totalPrice)
      }
    } yield result

    def getTransaction(transactionId: UUID): DBIO[Option[Transaction]] = for {
      transaction <- QueryUtility.transactionsQuery.filter(_.id === transactionId).result.headOption
    } yield transaction

    def updateTransaction(transactionId: UUID, status: Int, staffId: UUID, customerId: UUID): DBIO[Option[Int]] = for {
      update <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(transaction => (transaction.staffId, transaction.customerId, transaction.status)).update(Some(staffId), Some(customerId), status)
      transactionStatus <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.status).result.headOption
      result <- update match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(transactionStatus)
      }
    } yield result

    def getTransaction(fromDate: Long, toDate: Long): DBIO[Seq[Transaction]] = for {
      transaction <- QueryUtility.transactionsQuery.filter(transaction => transaction.date >= fromDate && transaction.date <= toDate).result
    }yield transaction

    def getTransactionByPaymentStatus(paymentStatus: Int): DBIO[Seq[Transaction]] = for {
      transaction <- QueryUtility.transactionsQuery.filter(_.paymentStatus === paymentStatus).result
    }yield transaction

    def updatePaymentStatus(transactionId: UUID, paymentStatus: Int): DBIO[Int] = for {
      updatePaymentStatus <- QueryUtility.transactionsQuery.filter(_.id === transactionId).map(_.paymentStatus).update(paymentStatus)
    } yield updatePaymentStatus
  }

}
