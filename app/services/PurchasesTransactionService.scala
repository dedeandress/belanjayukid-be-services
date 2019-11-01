package services

import com.google.inject.Inject
import graphql.Context
import models.{CreatePurchasesTransactionResult, PurchasesTransaction}
import repositories.repositoryInterfaces.PurchasesTransactionRepository

import scala.concurrent.{ExecutionContext, Future}

class PurchasesTransactionService @Inject()(purchasesTransactionRepository: PurchasesTransactionRepository, implicit val executionContext: ExecutionContext){

  def createTransaction(context: Context): Future[CreatePurchasesTransactionResult] ={
    purchasesTransactionRepository.addPurchasesTransaction(PurchasesTransaction()).map{
      purchasesTransaction =>
        CreatePurchasesTransactionResult(purchasesTransaction._1, purchasesTransaction._2)
    }
  }

}
