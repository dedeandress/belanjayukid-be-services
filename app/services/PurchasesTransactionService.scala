package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.`type`.PurchasesTransactionsResult
import graphql.input.PurchasesTransactionInput
import models.{CreatePurchasesTransactionResult, Payment, PurchasesTransaction, PurchasesTransactionDetail, TransactionDetail}
import repositories.repositoryInterfaces.{PaymentRepository, PurchasesTransactionDetailRepository, PurchasesTransactionRepository}
import utilities.{JWTUtility, PaymentStatus, PurchasesTransactionStatus, TransactionDetailStatus}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionService @Inject()(purchasesTransactionRepository: PurchasesTransactionRepository, purchasesTransactionDetailRepository: PurchasesTransactionDetailRepository
                                            , paymentRepository: PaymentRepository, implicit val executionContext: ExecutionContext){

  def createTransaction(context: Context): Future[CreatePurchasesTransactionResult] ={
    paymentRepository.addPayment(Payment()).flatMap{
      id =>
        purchasesTransactionRepository.addPurchasesTransaction(PurchasesTransaction(paymentId = id)).map{
          purchasesTransaction =>
            CreatePurchasesTransactionResult(purchasesTransaction._1, purchasesTransaction._2)
        }
    }
  }

  def checkoutPurchases(context: Context, purchasesTransactionInput: PurchasesTransactionInput): Future[PurchasesTransactionsResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    var list = new mutable.MutableList[PurchasesTransactionDetail]()
    val purchasesTransactionId = UUID.fromString(purchasesTransactionInput.purchasesTransactionId)
    val staffId = UUID.fromString(purchasesTransactionInput.staffId)
    val supplierId = UUID.fromString(purchasesTransactionInput.supplierId)

    for (detail <- purchasesTransactionInput.detail) {
      val productDetailId = UUID.fromString(detail.productDetailId)
      if (list.exists(detail => detail.productDetailId == productDetailId)) {
        val numberOfPurchases = list.find(item => item.productDetailId == productDetailId).get.numberOfPurchases + detail.numberOfPurchase
        val index = list.indexWhere(item => item.productDetailId == productDetailId)
        val purchasesTransactionDetail = new PurchasesTransactionDetail(
          purchasesTransactionId = UUID.fromString(purchasesTransactionInput.purchasesTransactionId),
          numberOfPurchases = numberOfPurchases,
          productDetailId = productDetailId,
        )
        play.Logger.warn(s"index : $index, numberOfPurchases: $numberOfPurchases, purchasesTransactionDetail: ${purchasesTransactionDetail.toString}")
        list.update(index, purchasesTransactionDetail)
        play.Logger.warn(s"${list.toList}")
      }
      else {
        play.Logger.warn(s"add transactionDetail to list")
        list += PurchasesTransactionDetail(
          purchasesTransactionId = UUID.fromString(purchasesTransactionInput.purchasesTransactionId),
          numberOfPurchases = detail.numberOfPurchase,
          productDetailId = productDetailId,
        )
      }
    }

    purchasesTransactionRepository.getPurchasesTransactionStatus(purchasesTransactionId).flatMap{
      status =>
        play.Logger.warn(s"addTransactionDetails: updateStatus : $status")
        if (status.get == PurchasesTransactionStatus.INITIAL) {
          purchasesTransactionDetailRepository.addPurchasesTransactionDetails(purchasesTransactionId, list).flatMap {
            purchasesTransactionDetails =>
            purchasesTransactionRepository.updateTotalPrice(purchasesTransactionId).flatMap{
              totalPrice =>
                purchasesTransactionRepository.updatePurchasesTransaction(purchasesTransactionId, PurchasesTransactionStatus.ON_PROCESS, staffId, supplierId).flatMap{
                  updateStatus =>
                    Future.successful(PurchasesTransactionsResult(updateStatus.get, purchasesTransactionDetails, totalPrice))
                }
            }
          }
        }
        else {
          purchasesTransactionDetailRepository.findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId).flatMap{
            purchasesTransactionDetails =>
              purchasesTransactionRepository.updateTotalPrice(purchasesTransactionId).flatMap{
                totalPrice =>
                  Future.successful(PurchasesTransactionsResult(status.get, purchasesTransactionDetails, totalPrice))
              }
          }
        }
    }
  }

  def completePaymentPurchases(context: Context, id: String, amountOfPayment: BigDecimal): Future[PurchasesTransactionsResult] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val purchasesTransactionId = UUID.fromString(id)
    val purchasesTransactionStatus = purchasesTransactionRepository.getPurchasesTransactionStatus(purchasesTransactionId)

    purchasesTransactionStatus.flatMap{
      status =>
        if(status.get == PurchasesTransactionStatus.ON_PROCESS){
          purchasesTransactionRepository.updateStock(purchasesTransactionId).flatMap{
            _=>
              purchasesTransactionRepository.updatePurchasesTransactionStatus(purchasesTransactionId, PurchasesTransactionStatus.COMPLETED).flatMap{
                status =>
                  purchasesTransactionDetailRepository.findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId).flatMap{
                    purchasesTransactionDetails =>
                      purchasesTransactionRepository.getTotalPrice(purchasesTransactionId).flatMap{
                        totalPrice =>
                          var paymentStatus = 0
                          var debt = totalPrice.get - amountOfPayment
                          if(debt <= 0) {
                            debt = 0
                            paymentStatus = PaymentStatus.PAID
                          }
                          else paymentStatus = PaymentStatus.DEBT
                          paymentRepository.updatePaymentPurchases(purchasesTransactionId, debt, amountOfPayment, paymentStatus).flatMap{
                            debt =>
                              Future.successful(PurchasesTransactionsResult(status.get, purchasesTransactionDetails, totalPrice.get, debt))
                          }
                      }
                  }
              }

          }
        }
        else {
          purchasesTransactionDetailRepository.findPurchasesTransactionDetailByPurchasesTransactionId(purchasesTransactionId).flatMap{
            purchasesTransactionDetails =>
              purchasesTransactionRepository.getTotalPriceAndDebt(purchasesTransactionId).flatMap{
                result =>
                  Future.successful(PurchasesTransactionsResult(status.get, purchasesTransactionDetails, result._1, result._2))
              }
          }
        }
    }
  }

}
