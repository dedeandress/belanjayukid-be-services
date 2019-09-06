package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.input.ProductDetailInput
import graphql.Context
import models.ProductDetail
import repositories.repositoryInterfaces.ProductDetailRepository
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductDetailService @Inject()(productDetailRepository: ProductDetailRepository, implicit val executionContext: ExecutionContext){

  def findProductDetailByProductId(context: Context, productId: UUID): Future[Seq[ProductDetail]] = {
    JWTUtility.isAdmin(context)
    productDetailRepository.findProductDetailByProductId(productId)
  }

  def deleteProductDetail(context: Context, id: UUID): Future[Boolean] = {
    if(!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productDetailRepository.deleteProductDetail(id).map{
      case 0 => false
      case _ => true
    }
  }

  def addProductDetail(context: Context, productDetail: ProductDetailInput) : Future[ProductDetail] = {
    if(!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
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
