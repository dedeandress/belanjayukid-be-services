package graphql.resolvers

import graphql.Context
import javax.inject.Inject
import models.CreatePurchasesTransactionResult
import services.PurchasesTransactionService

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionResolver @Inject()(purchasesTransactionService: PurchasesTransactionService, implicit val executionContext: ExecutionContext){

  def createPurchasesTransaction(context: Context): Future[CreatePurchasesTransactionResult] = purchasesTransactionService.createTransaction(context)

}
