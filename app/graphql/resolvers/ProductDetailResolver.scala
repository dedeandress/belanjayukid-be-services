package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.ProductDetail
import services.ProductDetailService

import scala.concurrent.Future

class ProductDetailResolver @Inject()(productDetailService: ProductDetailService){

  def productDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = productDetailService.findProductDetailByProductId(productId)

  def deleteProductDetail(id: UUID): Future[Option[ProductDetail]] = productDetailService.updateProductDetailStatus(id)

}
