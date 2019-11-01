package services

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.`type`.TransactionsResult
import graphql.input.TransactionInput
import models.{CreateTransactionResult, Payment, Transaction, TransactionDetail, TransactionResult}
import repositories.repositoryInterfaces.{PaymentRepository, ProductDetailRepository, ProductsRepository, TransactionDetailRepository, TransactionRepository}
import utilities.{JWTUtility, PaymentStatus, TransactionDetailStatus, TransactionStatus}

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class TransactionService @Inject()(transactionRepository: TransactionRepository, transactionDetailRepository: TransactionDetailRepository
                                   , productRepository: ProductsRepository, productDetailRepository: ProductDetailRepository
                                   , paymentRepository: PaymentRepository, implicit val executionContext: ExecutionContext) {

  def addTransaction(context: Context): Future[CreateTransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    paymentRepository.addPayment(Payment()).flatMap{
      paymentId =>
        transactionRepository.addTransaction(new Transaction(paymentId = paymentId)).flatMap {
          id =>
            transactionRepository.getTransactionStatus(id).map {
              status =>
                CreateTransactionResult(id, status.get)
            }
        }
    }

  }

  def updateTransaction(context: Context, transactionId: UUID, status: Int): Future[Option[Int]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.updateTransactionStatus(transactionId, status)
  }

  def completePayment(context: Context, id: String, amountOfPayment: BigDecimal): Future[TransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val transactionId = UUID.fromString(id)
    val transactionStatus = transactionRepository.getTransactionStatus(transactionId)

    transactionStatus.flatMap {
      trStatus =>
        play.Logger.warn(s"completePayment: Status = $trStatus")
        if (trStatus.get == TransactionStatus.ON_PROCESS) {
          transactionRepository.updateStock(transactionId).flatMap {
            _ =>
              transactionRepository.updateTransactionStatus(transactionId, TransactionStatus.ON_CHECKER).flatMap {
                status =>
                  transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).flatMap {
                    transactionDetailList =>
                      transactionRepository.updateTotalPrice(transactionId).flatMap{
                        totalPrice =>
                          var paymentStatus = 0
                          var debt = totalPrice - amountOfPayment
                          if(debt<=0) {
                            debt = 0
                            paymentStatus = PaymentStatus.PAID
                          }
                          else paymentStatus = PaymentStatus.DEBT
                          paymentRepository.updatePayment(transactionId = transactionId, debt =  debt, amountOfPayment = amountOfPayment, paymentStatus).flatMap{
                            debt =>
                              Future.successful(TransactionResult(status.get, transactionDetailList, totalPrice, debt))
                          }
                      }
                  }
              }
          }
        } else {
          transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).flatMap {
            transactionDetails =>
              transactionRepository.getTotalPriceAndDebt(transactionId).flatMap{
                result => Future.successful(TransactionResult(trStatus.get, transactionDetails, result._1, result._2))
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

    transactionRepository.getTransactionStatus(transactionId).flatMap {
      status =>
        play.Logger.warn(s"addTransactionDetails: updateStatus : $status")
        if (status.get == TransactionStatus.INITIAL) {
          transactionDetailRepository.addTransactionDetails(list.toList, transactionId).flatMap {
            transactionDetails =>
              transactionRepository.updateTotalPrice(transactionId).flatMap{
                totalPrice =>
                  transactionRepository.updateTransaction(transactionId, TransactionStatus.ON_PROCESS, staffId, customerId).flatMap{
                    updateStatus =>
                      Future.successful(TransactionResult(updateStatus.get, transactionDetails, totalPrice))
                  }
              }
          }
        } else {
          transactionDetailRepository.findTransactionDetailByTransactionId(transactionId).flatMap {
            details =>
              transactionRepository.updateTotalPrice(transactionId).flatMap{
                totalPrice=>
                  Future.successful(TransactionResult(status.get, details, totalPrice))
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
