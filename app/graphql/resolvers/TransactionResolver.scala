package graphql.resolvers

import graphql.input.TransactionInput
import javax.inject.Inject
import services.TransactionService

import scala.concurrent.{ExecutionContext, Future}

class TransactionResolver @Inject()(transactionService: TransactionService, implicit val executionContext: ExecutionContext) {

  def createTransaction(): Future[Int] = transactionService.addTransaction()

  def createTransactionDetail(transactionInput: TransactionInput): Future[Int] = transactionService.addTransactionDetail(transactionInput)

  def completePayment(transactionId: String) = transactionService.completePayment(transactionId)

}
