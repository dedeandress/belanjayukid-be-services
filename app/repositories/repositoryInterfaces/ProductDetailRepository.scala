package repositories.repositoryInterfaces

import java.util.UUID

import models.ProductDetail

import scala.concurrent.Future

trait ProductDetailRepository {

  def findProductDetail(id: UUID): Future[Option[ProductDetail]]

  def addProductDetail(productDetail: ProductDetail): Future[ProductDetail]

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]]

  def updateProductDetailStatus(id: UUID): Future[Option[ProductDetail]]
}
