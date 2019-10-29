package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.`type`.TransactionsResult
import graphql.input.TransactionInput
import models.{CreateTransactionResult, Transaction, TransactionDetail, TransactionResult}
import repositories.repositoryInterfaces.{ProductDetailRepository, ProductsRepository, TransactionDetailRepository, TransactionRepository}
import utilities.{JWTUtility, TransactionDetailStatus, TransactionStatus}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TransactionService @Inject()(transactionRepository: TransactionRepository, transactionDetailRepository: TransactionDetailRepository
                                   , productRepository: ProductsRepository, productDetailRepository: ProductDetailRepository
                                   , implicit val executionContext: ExecutionContext) {

  def addTransaction(context: Context): Future[CreateTransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.addTransaction(new Transaction()).flatMap {
      id =>
        transactionRepository.getTransactionStatus(id).map {
          status =>
            CreateTransactionResult(id, status.get)
        }
    }
  }

  def updateTransaction(context: Context, transactionId: UUID, status: Int): Future[Option[Int]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.updateTransactionStatus(transactionId, status)
  }

  def completePayment(context: Context, transactionId: String): Future[TransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val transactionStatus = transactionRepository.getTransactionStatus(UUID.fromString(transactionId))

    transactionStatus.flatMap {
      trStatus =>
        if (trStatus.get == TransactionStatus.ON_PROCESS) {
          transactionRepository.updateStock(UUID.fromString(transactionId)).flatMap {
            _ =>
              transactionRepository.updateTransactionStatus(UUID.fromString(transactionId), TransactionStatus.ON_CHECKER).flatMap {
                status =>
                  transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).flatMap {
                    transactionDetailList =>
                      transactionRepository.updateTotalPrice(UUID.fromString(transactionId)).flatMap{
                        totalPrice => Future.successful(TransactionResult(status.get, transactionDetailList, totalPrice))
                      }
                  }
              }
          }
        } else {
          transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).flatMap {
            transactionDetails =>
              transactionRepository.updateTotalPrice(UUID.fromString(transactionId)).flatMap{
                totalPrice => Future.successful(TransactionResult(trStatus.get, transactionDetails, totalPrice))
              }
          }
        }
    }
  }

  def addTransactionDetails(context: Context, transactionInput: TransactionInput): Future[TransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    var list = new mutable.MutableList[TransactionDetail]()
    val transactionId = UUID.fromString(transactionInput.transactionId)
    val staffId = UUID.fromString(transactionInput.staffId)
    val customerId = UUID.fromString(transactionInput.customerId)
    for (detail <- transactionInput.detail) {
      val productDetailId = UUID.fromString(detail.productDetailId)
      if (list.exists(item => item.productDetailId == productDetailId)){
        val numberOfPurchases = list.find(item => item.productDetailId == productDetailId).get.numberOfPurchases + detail.numberOfPurchase
        val index = list.indexWhere(item => item.productDetailId == productDetailId)
        val transactionDetail = new TransactionDetail(
          transactionId = UUID.fromString(transactionInput.transactionId),
          numberOfPurchases = numberOfPurchases,
          productDetailId = productDetailId,
          status = TransactionDetailStatus.NOT_EMPTY
        )
        play.Logger.warn(s"index : $index, numberOfPurchases: $numberOfPurchases, transactionDetail: ${transactionDetail.toString}")
        list.update(index, transactionDetail)
        play.Logger.warn(s"${list.toList}")
      }
      else {
        play.Logger.warn(s"add transactionDetail to list")
        list.+=(new TransactionDetail(
          transactionId = UUID.fromString(transactionInput.transactionId),
          numberOfPurchases = detail.numberOfPurchase,
          productDetailId = UUID.fromString(detail.productDetailId),
          status = TransactionDetailStatus.NOT_EMPTY
        ))
      }
    }

    val addTransaction = for {
      status <- transactionRepository.getTransactionStatus(transactionId)
      updateStatus <- if (status.get == TransactionStatus.INITIAL) {
        transactionRepository.updateTransaction(transactionId, TransactionStatus.ON_PROCESS, staffId, customerId)
      } else Future.successful(status)
      _ <- if (status.get == TransactionStatus.INITIAL) transactionDetailRepository.addTransactionDetails(list.toList) else Future.successful(status)
    } yield updateStatus.get

    addTransaction.flatMap {
      status =>
        if (status == TransactionStatus.ON_PROCESS) {
          val result = transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).map {
            details =>
              for (detail <- details) {
                for {
                  productDetail <- productDetailRepository.findProductDetail(detail.productDetailId)
                  product <- productRepository.findProduct(productDetail.get.productId)
                  _ <- if (product.get.stock < (detail.numberOfPurchases * productDetail.get.value)) transactionDetailRepository.updateTransactionDetailStatus(detail.id, TransactionDetailStatus.EMPTY) else Future.successful(TransactionDetailStatus.NOT_EMPTY)
                } yield ()
              }
          }
          result.flatMap {
            _ =>
              transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).flatMap {
                transactionDetailList =>
                  transactionRepository.updateTotalPrice(transactionId).flatMap{
                    totalPrice => Future.successful(TransactionResult(status, transactionDetailList, totalPrice))
                  }
              }
          }
        } else {
          transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).flatMap {
            details =>
              transactionRepository.updateTotalPrice(transactionId).flatMap{
                totalPrice=>
                  Future.successful(TransactionResult(status, details, totalPrice))
              }
          }
        }
    }
  }

  def updateStaff(context: Context, transactionId: UUID, staffId: UUID): Future[Option[UUID]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.updateStaff(transactionId, staffId)
  }

  def updateCustomer(context: Context, transactionId: UUID, customerId: UUID): Future[Option[UUID]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.updateCustomer(transactionId, customerId)
  }

  def getAllTransaction(context: Context, status: Int): Future[Seq[Transaction]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getTransactions(status)
  }

  def getTransaction(context: Context, transactionId: UUID): Future[Option[Transaction]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getTransaction(transactionId)
  }

  def getTransactionsWithLimit(context: Context, limit: Int): Future[TransactionsResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getAllTransactionWithLimit(limit)
  }

}
