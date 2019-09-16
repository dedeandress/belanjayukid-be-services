package services

import java.util.UUID

import com.google.inject.Inject
import graphql.input.TransactionInput
import models.{Transaction, TransactionDetail}
import repositories.repositoryInterfaces.{ProductDetailRepository, ProductsRepository, TransactionDetailRepository, TransactionRepository}
import utilities.{TransactionDetailStatus, TransactionStatus}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TransactionService @Inject()(transactionRepository: TransactionRepository, transactionDetailRepository: TransactionDetailRepository
                                   , productRepository: ProductsRepository, productDetailRepository: ProductDetailRepository
                                   , implicit val executionContext: ExecutionContext){

  def addTransaction(): Future[Int] = transactionRepository.addTransaction(new Transaction())

  def updateTransaction(transactionId: UUID, status: Int): Future[Option[Int]] = transactionRepository.updateTransactionStatus(transactionId, status)

  def addTransactionDetail(transactionInput: TransactionInput): Future[Int] = {
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

    for {
      status <- transactionRepository.getTransactionStatus(transactionId)
      updateStatus <- if(status.get == TransactionStatus.INITIAL){
        transactionRepository.updateTransactionStatus(transactionId, TransactionStatus.ON_PROCESS)
      } else Future.successful(status)
      _ <- if (updateStatus.get == TransactionStatus.ON_PROCESS) transactionDetailRepository.addTransactionDetail(list.toList) else Future.successful(status)
    }yield updateStatus.get
  }

  def completePayment(transactionId: String): Future[Int] = {

    val transactionStatus = transactionRepository.getTransactionStatus(UUID.fromString(transactionId))

    transactionStatus.flatMap{
      trStatus => if (trStatus.get == TransactionStatus.ON_PROCESS){
        val result = transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).map{
          details=>
            for (detail <- details) {
              for {
                productDetail <- productDetailRepository.findProductDetail(detail.productDetailId)
                product <- productRepository.findProduct(productDetail.get.productId)
                _ <- productRepository.updateStock(product.get.id, product.get.stock - (detail.numberOfPurchases * productDetail.get.value))
              } yield ()
            }
        }

        result.flatMap{
          _ =>
            transactionRepository.updateTransactionStatus(UUID.fromString(transactionId), TransactionStatus.ON_CHECKER).flatMap{
              status => Future.successful(status.get)
            }
        }
      }else Future.successful(trStatus.get)
    }
  }
}
