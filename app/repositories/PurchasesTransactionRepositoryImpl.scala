package repositories

import java.util.UUID

import com.google.inject.Inject
import errors.NotFound
import models.PurchasesTransaction
import modules.AppDatabase
import repositories.repositoryInterfaces.PurchasesTransactionRepository
import utilities.{QueryUtility, TransactionDetailStatus}

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends PurchasesTransactionRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): Future[(UUID, Int)] = db.run(Actions.addPurchasesTransaction(purchasesTransaction))

  override def getPurchasesTransactionStatus(id: UUID): Future[Option[Int]] = db.run(Actions.getPurchasesTransactionStatus(id))

  override def getTotalPriceAndDebt(purchasesTransactionId: UUID): Future[(BigDecimal, BigDecimal)] = {
    db.run(sql"select pt.total_price, p.debt from purchases_transactions pt join payments p on pt.payment_id = p.id where pt.id::varchar = ${purchasesTransactionId.toString()}".as[(BigDecimal, BigDecimal)]).map{
      result =>
        (result.head._1, result.head._2)
    }
  }

  override def updatePurchasesTransaction(purchasesTransactionId: UUID, status: Int, staffId: UUID, supplierId: UUID): Future[Option[Int]] = {
    db.run(Actions.updatePurchasesTransaction(purchasesTransactionId, status, staffId, supplierId))
  }

  override def updateStock(transactionId: UUID): Future[Unit] = {
    db.run(sql"select p.id::varchar, p.stock, ptd.number_of_purchases, pd.value from purchases_transaction_detail ptd join product_detail pd on ptd.product_detail_id = pd.id join products p on pd.product_id = p.id where ${transactionId.toString()} = ptd.purchases_transaction_id::varchar".as[(String, Int, Int, Int)]).flatMap {
      list =>
        play.Logger.warn(s"list of transactionDetail : ${list.toString()}")
        var stock = 0
        val update = for (item <- list) yield {
          stock = item._2 + (item._3 * item._4)
          play.Logger.warn(s"stock : $stock")
          QueryUtility.productQuery.filter(_.id === UUID.fromString(item._1)).map(_.stock).update(stock)
        }
        db.run(DBIO.seq(update: _*))
    }
  }

  override def updateTotalPrice(purchasesTransactionId: UUID): Future[BigDecimal] = {
    db.run(sql"select purchase_price, number_of_purchases from purchases_transaction_detail ptd join product_detail pd on ptd.product_detail_id = pd.id join products p on pd.product_id = p.id where ${purchasesTransactionId.toString()} = ptd.purchases_transaction_id::varchar".as[(BigDecimal, Int)]).flatMap {
      list =>
        play.Logger.warn(list.toString())
        var totalPrice: BigDecimal = 0
        for (item <- list) {
          totalPrice += item._1 * item._2
        }
        play.Logger.warn(s"totalPrice: $totalPrice")
        db.run(Actions.updateTotalPrice(purchasesTransactionId, totalPrice))
    }
  }

  override def updatePurchasesTransactionStatus(purchasesTransactionId: UUID, status: Int): Future[Option[Int]] = {
    db.run(Actions.updatePurchasesTransaction(purchasesTransactionId, status))
  }

  override def getTotalPrice(purchasesTransactionId: UUID): Future[Option[BigDecimal]] = {
    db.run(Actions.getTotalPrice(purchasesTransactionId))
  }

  object Actions {

    def addPurchasesTransaction(purchasesTransaction: PurchasesTransaction): DBIO[(UUID, Int)] = for{
      id <- QueryUtility.purchasesTransactionQuery returning QueryUtility.purchasesTransactionQuery.map(_.id) += purchasesTransaction
      status <- getPurchasesTransactionStatus(id)
    }yield (id,status.get)

    def getPurchasesTransactionStatus(transactionId: UUID): DBIO[Option[Int]] = for {
      status <- QueryUtility.purchasesTransactionQuery.filter(_.id === transactionId).map(_.status).result.headOption
    } yield status

    def getAllPurchasesTransaction(status: Int): DBIO[Seq[PurchasesTransaction]] = for {
      purchasesTransactions <- QueryUtility.purchasesTransactionQuery.filter(_.status === status).result
    } yield purchasesTransactions

    def getPurchasesTransaction(purchasesTransactionId: UUID): DBIO[Option[PurchasesTransaction]] = for {
      purchasesTransaction <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).result.headOption
    } yield purchasesTransaction

    def updatePurchasesTransaction(purchasesTransactionId: UUID, status: Int): DBIO[Option[Int]] = for {
      _ <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.status).update(status)
      transactionStatus <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.status).result.headOption
    } yield transactionStatus

    def updatePurchasesTransaction(purchasesTransactionId: UUID, status: Int, staffId: UUID, supplierId: UUID): DBIO[Option[Int]] = for {
      update <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(purchasesTransaction => (purchasesTransaction.staffId, purchasesTransaction.supplierId, purchasesTransaction.status)).update(Some(staffId), Some(supplierId), status)
      transactionStatus <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.status).result.headOption
      result <- update match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(transactionStatus)
      }
    } yield result

    def updateTotalPrice(purchasesTransactionId: UUID, totalPrice: BigDecimal): DBIO[BigDecimal] = for {
      update <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.totalPrice).update(totalPrice)
      transaction <- getPurchasesTransaction(purchasesTransactionId)
      result <- update match {
        case 0 => DBIO.failed(NotFound("not found transaction id"))
        case _ => DBIO.successful(transaction.get.totalPrice)
      }
    } yield result

    def getTotalPrice(purchasesTransactionId: UUID): DBIO[Option[BigDecimal]] = for{
      totalPrice <- QueryUtility.purchasesTransactionQuery.filter(_.id === purchasesTransactionId).map(_.totalPrice).result.headOption
    } yield totalPrice

  }

}
