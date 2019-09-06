package services

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductDetailInput
import models.ProductDetail
import repositories.repositoryInterfaces.ProductDetailRepository

import scala.concurrent.{ExecutionContext, Future}

class ProductDetailService @Inject()(productDetailRepository: ProductDetailRepository, implicit val executionContext: ExecutionContext){

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = productDetailRepository.findProductDetailByProductId(productId)

  def deleteProductDetail(id: UUID): Future[Boolean] = {
    productDetailRepository.deleteProductDetail(id).map{
      case 0 => false
      case _ => true
    }
  }

  def addProductDetail(productDetail: ProductDetailInput) : Future[ProductDetail] = {
    productDetailRepository.addProductDetail(
      new ProductDetail(
        value = productDetail.value,
        purchasePrice = productDetail.purchasePrice,
        sellingPrice = productDetail.sellingPrice,
        productId = UUID.fromString(productDetail.productId),
        productStockId = UUID.fromString(productDetail.productStockId)
      )
    )
  }

}
