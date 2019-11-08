package graphql.resolvers

import java.util.UUID

import graphql.Context
import graphql.`type`.{RefundTransactionResult, TransactionsResult}
import graphql.input.{CheckTransactionInput, TransactionInput}
import javax.inject.Inject
import models.{CreateTransactionResult, Transaction, TransactionResult}
import services.TransactionService

import scala.concurrent.{ExecutionContext, Future}

class TransactionResolver @Inject()(transactionService: TransactionService, implicit val executionContext: ExecutionContext) {

  def createTransaction(context: Context): Future[CreateTransactionResult] = transactionService.addTransaction(context)

  def createTransactionDetail(context: Context, transactionInput: TransactionInput): Future[TransactionResult] = transactionService.addTransactionDetails(context, transactionInput)

  def completePayment(context: Context, transactionId: String, amountOfPayment: BigDecimal): Future[TransactionResult] = transactionService.completePayment(context,
    transactionId, amountOfPayment)

  def getTransactions(context: Context, status: Int): Future[Seq[Transaction]] = transactionService.getAllTransaction(context, status)

  def getTransaction(context: Context, transactionId: UUID): Future[Option[Transaction]] = transactionService.getTransaction(context, transactionId)

  def updateStaff(context: Context, transactionId: UUID, staffId: UUID): Future[Option[UUID]] = transactionService.updateStaff(context, transactionId, staffId)

  def updateCustomer(context: Context, transactionId: UUID, customerId: UUID): Future[Option[UUID]] = transactionService.updateCustomer(context, transactionId, customerId)

  def getTransactionsWithLimit(context: Context, limit: Int): Future[TransactionsResult] = transactionService.getTransactionsWithLimit(context, limit)

  def checkTransaction(context: Context, checkTransaction: CheckTransactionInput): Future[Option[Int]] = transactionService.checkTransaction(context, checkTransaction)

  def refundTransaction(context: Context, transactionId: String): Future[RefundTransactionResult] = transactionService.refundTransaction(context, transactionId)

  def completeRefund(context: Context, transactionId: String): Future[Option[Transaction]] = transactionService.completeRefund(context, transactionId)

}
