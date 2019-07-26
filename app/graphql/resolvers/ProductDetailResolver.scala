package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.ProductDetail
import services.ProductDetailService

import scala.concurrent.Future

class ProductDetailResolver @Inject()(productDetailService: ProductDetailService){

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = productDetailService.findProductDetailByProductId(productId)

}
