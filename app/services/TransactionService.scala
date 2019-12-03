package services

import java.util.UUID

import com.google.inject.Inject
import errors.{AuthorizationException, BadRequest, NotFound}
import graphql.Context
import graphql.`type`.{RefundTransactionResult, TransactionsResult}
import graphql.input.{CheckTransactionInput, TransactionInput}
import models.{CreateTransactionResult, Payment, Transaction, TransactionDetail, TransactionResult}
import repositories.repositoryInterfaces.{PaymentRepository, ProductDetailRepository, ProductsRepository, TransactionDetailRepository, TransactionRepository}
import utilities.{JWTUtility, PaymentStatus, TransactionDetailStatus, TransactionStatus, Utility}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

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

  def updateTransaction(context: Context, transactionId: String, status: Int): Future[Option[Int]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.updateTransactionStatus(Utility.checkUUID(transactionId, "Transaction"), status)
  }

  def completePayment(context: Context, id: String, amountOfPayment: BigDecimal): Future[TransactionResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
      val transactionId = Utility.checkUUID(id, "Transaction")
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
                        transactionRepository.updateTotalPrice(transactionId, TransactionDetailStatus.NOT_EMPTY).flatMap{
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
    val transactionId = Utility.checkUUID(transactionInput.transactionId, "Transaction")
    val staffId = Utility.checkUUID(transactionInput.staffId, "Staff")
    val customerId = Utility.checkUUID(transactionInput.customerId, "Customer")
    for (detail <- transactionInput.detail) {
      val productDetailId = Utility.checkUUID(detail.productDetailId, "Product Detail")
      if (list.exists(item => item.productDetailId == productDetailId)){
        val numberOfPurchases = list.find(item => item.productDetailId == productDetailId).get.numberOfPurchases + detail.numberOfPurchase
        val index = list.indexWhere(item => item.productDetailId == productDetailId)
        val transactionDetail = new TransactionDetail(
          transactionId = Utility.checkUUID(transactionInput.transactionId, "Transaction"),
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
          transactionId = Utility.checkUUID(transactionInput.transactionId, "Transaction"),
          numberOfPurchases = detail.numberOfPurchase,
          productDetailId = Utility.checkUUID(detail.productDetailId, "Product Detail"),
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
              transactionRepository.updateTotalPrice(transactionId, TransactionDetailStatus.NOT_EMPTY).flatMap{
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
              transactionRepository.updateTotalPrice(transactionId, TransactionDetailStatus.NOT_EMPTY).flatMap{
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

  def getTransaction(context: Context, transactionId: String): Future[Option[Transaction]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getTransaction(Utility.checkUUID(transactionId, "Transaction"))
  }

  def getTransactionsWithLimit(context: Context, limit: Int, status: Int): Future[TransactionsResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getAllTransactionWithLimit(limit, status)
  }

  def getTransactionWithRange(context: Context, fromDate: Long, toDate: Long): Future[Seq[Transaction]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getTransactions(fromDate, toDate)
  }

  def checkTransaction(context: Context, checkTransaction: CheckTransactionInput): Future[Option[Int]] ={
    if (!JWTUtility.isAdminOrChecker(context)) throw AuthorizationException("You are not authorized")
    val transactionId = Utility.checkUUID(checkTransaction.transactionId, "Transaction")
    var transactionStatus = TransactionStatus.ON_PROCESS
    val searchRefundTransaction = checkTransaction.transactionDetail.find(_.status == TransactionDetailStatus.REFUNDED)
    if(searchRefundTransaction.nonEmpty) transactionStatus = TransactionStatus.ON_REFUND else transactionStatus = TransactionStatus.COMPLETED
    transactionDetailRepository.updateTransactionDetailStatusBulk(checkTransaction.transactionDetail).flatMap{
      _ => transactionRepository.updateTransactionStatus(transactionId, transactionStatus)
    }
  }

  def refundTransaction(context: Context, id: String): Future[RefundTransactionResult] = {
    if (!JWTUtility.isAdminOrChecker(context)) throw AuthorizationException("You are not authorized")
    val transactionId = Utility.checkUUID(id, "Transaction")
    transactionDetailRepository.findTransactionDetailByTransactionIdByStatus(transactionId, TransactionDetailStatus.REFUNDED).flatMap{
      result =>
        transactionRepository.updateTotalPrice(transactionId, TransactionDetailStatus.COMPLETED).flatMap{
          totalPrice=>
            Future.successful(RefundTransactionResult(result._1, result._2, totalPrice))
        }
    }
  }

  def completeRefund(context: Context, id: String): Future[Option[Transaction]] = {
    if (!JWTUtility.isAdminOrChecker(context)) throw AuthorizationException("You are not authorized")
    val transactionId = Utility.checkUUID(id, "Transaction")
    transactionRepository.getTransactionStatus(transactionId).flatMap {
      case Some(status) =>
        if (status == TransactionStatus.ON_REFUND) {
          transactionRepository.updateTransactionStatus(transactionId, TransactionStatus.COMPLETED).flatMap {
            _ =>
              transactionRepository.getTransaction(transactionId)
          }
        }
        else {
          play.Logger.error(s"Bad Request Transaction ID: $transactionId status is $status")
          throw BadRequest(s"Transaction ID: $transactionId status is $status")
        }
      case None => throw NotFound("Transaction Not Found")
    }
  }

  def getTransactionByPaymentStatus(context: Context, paymentStatus: Int): Future[Seq[Transaction]] = {
    if (!JWTUtility.isAdminOrChecker(context)) throw AuthorizationException("You are not authorized")
    transactionRepository.getTransactionsByPaymentStatus(paymentStatus)
  }

  def payOffDebt(context: Context, id: String, amountOfPayment: BigDecimal): Future[Option[Transaction]] = {
    if (!JWTUtility.isAdminOrChecker(context)) throw AuthorizationException("You are not authorized")
    val transactionId = Utility.checkUUID(id, "Transaction")
    transactionRepository.getTransaction(transactionId).flatMap{
      case Some(transaction) =>
        paymentRepository.findById(transaction.paymentId).flatMap{
          case Some(payment) =>
            if (amountOfPayment >= payment.debt) {
              transactionRepository.updatePaymentStatus(transaction.id, PaymentStatus.PAID).flatMap{
                case _ => transactionRepository.getTransaction(transaction.id)
                case 0 => throw NotFound("Payment Failed")
              }
            }else throw NotFound("Payment Failed : insufficient fund")
          case None => throw NotFound("Payment Not Found")
        }
      case None => throw NotFound("Transaction Not Found")
    }
  }

}
