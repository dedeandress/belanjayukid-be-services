package services

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import models.ProductDetail
import repositories.repositoryInterfaces.ProductDetailRepository
import utilities.JWTUtility

import scala.concurrent.Future

class ProductDetailService @Inject()(productDetailRepository: ProductDetailRepository){

  def findProductDetailByProductId(context: Context, productId: UUID): Future[Seq[ProductDetail]] = {
    JWTUtility.isAdmin(context)
    productDetailRepository.findProductDetailByProductId(productId)
  }

}
