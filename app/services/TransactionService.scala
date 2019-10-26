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

  def addTransactionDetailOldFlow(context: Context, transactionInput: TransactionInput): Future[Int] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    var list = new mutable.MutableList[TransactionDetail]()
    val transactionId = UUID.fromString(transactionInput.transactionId)
    for (detail <- transactionInput.detail) {
      list.+=(new TransactionDetail(
        transactionId = UUID.fromString(transactionInput.transactionId),
        numberOfPurchases = detail.numberOfPurchase,
        productDetailId = UUID.fromString(detail.productDetailId),
        subTotalPrice = detail.subTotalPrice,
        status = TransactionDetailStatus.NOT_EMPTY
      ))
    }
    println(list.size)

    for {
      status <- transactionRepository.getTransactionStatus(transactionId)
      updateStatus <- if (status.get == TransactionStatus.INITIAL) {
        transactionRepository.updateTransactionStatus(transactionId, TransactionStatus.ON_PROCESS)
      } else Future.successful(status)
      _ <- if (updateStatus.get == TransactionStatus.ON_PROCESS) transactionDetailRepository.addTransactionDetails(list.toList) else Future.successful(status)
    } yield updateStatus.get
  }

  def completePayment(context: Context, transactionId: String): Future[TransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val transactionStatus = transactionRepository.getTransactionStatus(UUID.fromString(transactionId))

    transactionStatus.flatMap {
      trStatus =>
        if (trStatus.get == TransactionStatus.ON_PROCESS) {
          val result = transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).map {
            details =>
              for (detail <- details) {
                for {
                  productDetail <- productDetailRepository.findProductDetail(detail.productDetailId)
                  product <- productRepository.findProduct(productDetail.get.productId)
                  _ <- if (detail.status == TransactionDetailStatus.NOT_EMPTY) productRepository.updateStock(product.get.id, product.get.stock - (detail.numberOfPurchases * productDetail.get.value)) else Future.successful(TransactionDetailStatus.EMPTY)
                } yield ()
              }
          }

          result.flatMap {
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
    for (detail <- transactionInput.detail) {
      list.+=(new TransactionDetail(
        transactionId = UUID.fromString(transactionInput.transactionId),
        numberOfPurchases = detail.numberOfPurchase,
        productDetailId = UUID.fromString(detail.productDetailId),
        subTotalPrice = detail.subTotalPrice,
        status = TransactionDetailStatus.NOT_EMPTY
      ))
    }

    val addTransaction = for {
      status <- transactionRepository.getTransactionStatus(transactionId)
      updateStatus <- if (status.get == TransactionStatus.INITIAL) {
        transactionRepository.updateTransaction(transactionId, TransactionStatus.ON_PROCESS, staffId)
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
