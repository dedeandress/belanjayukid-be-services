package graphql.resolvers

import java.util.UUID

import graphql.Context
import graphql.`type`.PurchasesTransactionsResult
import graphql.input.PurchasesTransactionInput
import javax.inject.Inject
import models.CreatePurchasesTransactionResult
import services.PurchasesTransactionService

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionResolver @Inject()(purchasesTransactionService: PurchasesTransactionService, implicit val executionContext: ExecutionContext){

  def createPurchasesTransaction(context: Context): Future[CreatePurchasesTransactionResult] = purchasesTransactionService.createTransaction(context)

  def checkout(context: Context, purchasesTransactionInput: PurchasesTransactionInput): Future[PurchasesTransactionsResult] = purchasesTransactionService.checkoutPurchases(context, purchasesTransactionInput)

  def completePayment(context: Context, purchasesTransactionId: String, amountOfPayment: BigDecimal): Future[PurchasesTransactionsResult] = purchasesTransactionService.completePaymentPurchases(context, purchasesTransactionId, amountOfPayment)

}
