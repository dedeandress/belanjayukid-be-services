package repositories

import java.util.UUID

import com.google.inject.Inject
import models.ProductDetail
import modules.AppDatabase
import repositories.repositoryInterfaces.ProductDetailRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends ProductDetailRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def findProductDetail(id: UUID): Future[Option[ProductDetail]] = db.run(Actions.findProductDetail(id))

  override def addProductDetail(productDetail: ProductDetail): Future[ProductDetail] = db.run(Actions.addProductDetail(productDetail))

  override def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = db.run(Actions.findProductDetailByProductId(productId))

  object Actions {

    def findProductDetail(id: UUID): DBIO[Option[ProductDetail]] = for{
      productDetail <- QueryUtility.productDetailQuery.filter(_.id === id).result.headOption
    }yield productDetail

    def addProductDetail(productDetail: ProductDetail): DBIO[ProductDetail] = for{
      id <- QueryUtility.productDetailQuery returning QueryUtility.productDetailQuery.map(_.id) += productDetail
      productDetail <- findProductDetail(id)
    }yield productDetail.get

    def findProductDetailByProductId(productId: UUID): DBIO[Seq[ProductDetail]] = for{
      productDetails <- QueryUtility.productDetailQuery.filter(_.productId === productId).result
    }yield productDetails

  }
}
