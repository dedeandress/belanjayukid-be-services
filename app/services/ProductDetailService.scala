package services

import java.util.UUID

import com.google.inject.Inject
import models.ProductDetail
import repositories.repositoryInterfaces.ProductDetailRepository

import scala.concurrent.Future

class ProductDetailService @Inject()(productDetailRepository: ProductDetailRepository){

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = productDetailRepository.findProductDetailByProductId(productId)

  def updateProductDetailStatus(id: UUID): Future[Option[ProductDetail]] = productDetailRepository.updateProductDetailStatus(id)

}
