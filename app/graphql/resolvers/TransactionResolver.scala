package graphql.resolvers

import graphql.Context
import graphql.input.TransactionInput
import javax.inject.Inject
import models.{CreateTransactionResult, TransactionResult}
import services.TransactionService

import scala.concurrent.{ExecutionContext, Future}

class TransactionResolver @Inject()(transactionService: TransactionService, implicit val executionContext: ExecutionContext) {

  def createTransaction(context: Context): Future[CreateTransactionResult] = transactionService.addTransaction(context)

  def createTransactionDetail(context: Context, transactionInput: TransactionInput): Future[TransactionResult] = transactionService.addTransactionDetails(context, transactionInput)

  def completePayment(context: Context, transactionId: String): Future[TransactionResult] = transactionService.completePayment(context,
    transactionId)

}
