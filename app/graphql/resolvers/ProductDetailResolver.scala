package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductDetailInput
import graphql.Context
import models.ProductDetail
import services.ProductDetailService

import scala.concurrent.Future

class ProductDetailResolver @Inject()(productDetailService: ProductDetailService){

  def productDetailByProductId(context: Context, productId: UUID): Future[Seq[ProductDetail]] = productDetailService.findProductDetailByProductId(context, productId)

  def deleteProductDetail(context: Context, id: UUID): Future[Boolean] = productDetailService.deleteProductDetail(context, id)

  def addProductDetail(context: Context, productDetailInput: ProductDetailInput): Future[ProductDetail] = productDetailService.addProductDetail(context, productDetailInput)

}
