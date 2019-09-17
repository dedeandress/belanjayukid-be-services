package services

import java.util.UUID

import com.google.inject.Inject
import graphql.input.TransactionInput
import models.{Transaction, TransactionDetail, TransactionResult}
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
    println(list.size)

    for {
      status <- transactionRepository.getTransactionStatus(transactionId)
      updateStatus <- if(status.get == TransactionStatus.INITIAL){
        transactionRepository.updateTransactionStatus(transactionId, TransactionStatus.ON_PROCESS)
      } else Future.successful(status)
      _ <- if (updateStatus.get == TransactionStatus.ON_PROCESS) transactionDetailRepository.addTransactionDetail(list.toList) else Future.successful(status)
    }yield updateStatus.get
  }

  def completePayment(transactionId: String): Future[TransactionResult] = {

    val transactionStatus = transactionRepository.getTransactionStatus(UUID.fromString(transactionId))

    transactionStatus.flatMap{
      trStatus => if (trStatus.get == TransactionStatus.ON_PROCESS){
        val result = transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).map{
          details=>
            for (detail <- details) {
              for {
                productDetail <- productDetailRepository.findProductDetail(detail.productDetailId)
                product <- productRepository.findProduct(productDetail.get.productId)
                status <- if (product.get.stock < (detail.numberOfPurchases * productDetail.get.value)) transactionDetailRepository.updateTransactionDetailStatus(detail.id, TransactionDetailStatus.EMPTY) else Future.successful(TransactionDetailStatus.NOT_EMPTY)
                _ <- if (status == TransactionDetailStatus.NOT_EMPTY) productRepository.updateStock(product.get.id, product.get.stock - (detail.numberOfPurchases * productDetail.get.value)) else Future.successful(TransactionDetailStatus.EMPTY)
              } yield ()
            }
        }

        result.flatMap{
          _ =>
            transactionRepository.updateTransactionStatus(UUID.fromString(transactionId), TransactionStatus.ON_CHECKER).flatMap{
              status =>
                transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).flatMap {
                  transactionDetailList =>
                    Future.successful(TransactionResult(status.get, transactionDetailList))
                }
            }
        }
      }else{
        transactionDetailRepository.findTransactionDetailByTransactionId(UUID.fromString(transactionId)).flatMap{
          transactionDetails => Future.successful(TransactionResult(trStatus.get, transactionDetails))
        }
      }
    }
  }
}
