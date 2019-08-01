package repositories.repositoryInterfaces

import java.util.UUID

import graphql.input.{ProductInput}
import models.ProductDetail

import scala.concurrent.Future

trait ProductDetailRepository {

  def findProductDetail(id: UUID): Future[Option[ProductDetail]]

  def addProductDetail(productId: UUID, productDetail: ProductInput): Future[Unit]

  def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]]
}
