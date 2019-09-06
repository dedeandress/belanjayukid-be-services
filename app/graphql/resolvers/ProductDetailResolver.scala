package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductDetailInput
import models.ProductDetail
import services.ProductDetailService

import scala.concurrent.Future

class ProductDetailResolver @Inject()(productDetailService: ProductDetailService){

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = productDetailService.findProductDetailByProductId(productId)

  def deleteProductDetail(id: UUID): Future[Boolean] = productDetailService.deleteProductDetail(id)

  def addProductDetail(productDetailInput: ProductDetailInput): Future[ProductDetail] = productDetailService.addProductDetail(productDetailInput)

}
