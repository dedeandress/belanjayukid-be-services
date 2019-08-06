package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.input.ProductDetailInput
import models.ProductDetail
import repositories.repositoryInterfaces.ProductDetailRepository
import utilities.JWTUtility

import scala.concurrent.Future

class ProductDetailService @Inject()(productDetailRepository: ProductDetailRepository){

  def findProductDetailByProductId(context: Context, productId: UUID): Future[Seq[ProductDetail]] = {
    if(JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productDetailRepository.findProductDetailByProductId(productId)
  }

  def updateProductDetailStatus(context: Context, id: UUID): Future[Option[ProductDetail]] = {
    if(JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    productDetailRepository.updateProductDetailStatus(id)
  }

  def addProductDetail(context: Context, productId: UUID, productDetailInput: ProductDetailInput): Future[Option[ProductDetail]] = {
    if(JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    val productDetail = new ProductDetail(productId = productId, productStockId = UUID.fromString(productDetailInput.productStockId)
      , value = productDetailInput.value, sellingPrice = productDetailInput.sellingPrice, purchasePrice = productDetailInput.purchasePrice)
    productDetailRepository.addProductDetail(productDetail)
  }

}
