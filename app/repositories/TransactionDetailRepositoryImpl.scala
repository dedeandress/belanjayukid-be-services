package repositories

import java.util.UUID

import com.google.inject.Inject
import graphql.input.CheckTransactionDetailInput
import models.TransactionDetail
import modules.AppDatabase
import repositories.repositoryInterfaces.TransactionDetailRepository
import utilities.{QueryUtility, TransactionDetailStatus}

import scala.concurrent.{ExecutionContext, Future}

class TransactionDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends TransactionDetailRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addTransactionDetails(details: List[TransactionDetail], transactionId: UUID): Future[Seq[TransactionDetail]] = {
    play.Logger.warn(s"add transactionDetail with transactionId: $transactionId")
    play.Logger.warn(s"transactionDetails : ${details.toString()}")
    val insert = for (detail <- details) yield {
      QueryUtility.transactionDetailQuery += detail
    }
    db.run(DBIO.seq(insert: _*)).flatMap{
      _ =>
        db.run(sql"select td.id, pd.value, td.number_of_purchases, p.stock from transaction_detail td join product_detail pd on td.product_detail_id = pd.id join products p on pd.product_id = p.id where td.transaction_id::varchar = ${transactionId.toString()}".as[(String, Int, Int, Int)]).flatMap{
          list =>
            var status = 0
            val updateStatus = for (item <- list) yield {
              if (item._4 < (item._3 * item._2)) {
                status = TransactionDetailStatus.EMPTY
              }
              else status = TransactionDetailStatus.NOT_EMPTY
              play.Logger.warn(s"transactionDetailId: ${item._1}, status: $status")
              QueryUtility.transactionDetailQuery.filter(_.id === UUID.fromString(item._1)).map(_.status).update(status)
            }
            db.run(DBIO.seq(updateStatus: _*)).flatMap{
              _ => db.run(Action.findTransactionDetailByTransactionId(transactionId))
            }
        }
    }
  }

  override def updateTransactionDetailStatus(transactionId: UUID): Future[Unit] = {
    play.Logger.warn(s"update transactionDetail status with transactionId: $transactionId")
    db.run(sql"select td.id, pd.value, td.number_of_purchases, p.stock from transaction_detail td join product_detail pd on td.product_detail_id = pd.id join products p on pd.product_id = p.id where td.transaction_id::varchar = ${transactionId.toString()}".as[(String, Int, Int, Int)]).map{
      list =>
        var status = 0
        val updateStatus = for (item <- list) yield {
          if (item._4 < (item._3 * item._2)) {
            status = TransactionDetailStatus.EMPTY
          }
          else status = TransactionDetailStatus.NOT_EMPTY
          QueryUtility.transactionDetailQuery.filter(_.id === UUID.fromString(item._1)).map(_.status).update(status)
        }
        db.run(DBIO.seq(updateStatus: _*))
    }
  }

  override def findTransactionDetailByTransactionId(transactionId: UUID): Future[Seq[TransactionDetail]] = {
    play.Logger.warn(s"find transactionDetail with id: $transactionId")
    db.run(Action.findTransactionDetailByTransactionId(transactionId))
  }

  override def updateTransactionDetailStatusBulk(transactionDetails: Seq[CheckTransactionDetailInput]): Future[Unit] = {
    val updateStatus = for (item <- transactionDetails) yield {
      QueryUtility.transactionDetailQuery.filter(_.id === UUID.fromString(item.transactionDetailId)).map(_.status).update(item.status)
    }
    db.run(DBIO.seq(updateStatus: _*))
  }

  object Action {

    private def transactionDetailTableWithObject =
      (QueryUtility.transactionDetailQuery returning QueryUtility.transactionDetailQuery.map(_.id)).into((person, id) => person.copy(id = id))

    def insertAll(list: Seq[TransactionDetail]): Future[Seq[TransactionDetail]] = db.run{ transactionDetailTableWithObject ++= list }

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
