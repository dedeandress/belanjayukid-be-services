package repositories

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductInput
import models.ProductDetail
import modules.AppDatabase
import repositories.repositoryInterfaces.ProductDetailRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductDetailRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends ProductDetailRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def findProductDetail(id: UUID): Future[Option[ProductDetail]] = {
    play.Logger.warn("find ProductDetail")
    db.run(Actions.findProductDetail(id))
  }

  override def addProductDetail(productId: UUID, productInput: ProductInput): Future[Unit] = {
    play.Logger.warn("add ProductDetail")
    val insert = for(productDetail <- productInput.productDetailInput) yield {
        val productDetailRow = new ProductDetail(
          value = productDetail.value,
          purchasePrice = productDetail.purchasePrice,
          sellingPrice = productDetail.sellingPrice,
          productId = productId,
          productStockId = UUID.fromString(productDetail.productStockId))
        QueryUtility.productDetailQuery += productDetailRow
    }
    db.run(DBIO.seq(insert: _*))
  }

  override def findProductDetailByProductId(productId: UUID): Future[Seq[ProductDetail]] = {
    play.Logger.warn("findProductDetailByProductId")
    db.run(Actions.findProductDetailByProductId(productId))
  }

  override def updateProductDetailStatus(id: UUID): Future[Option[ProductDetail]] = {
    val q = for { p <- QueryUtility.productDetailQuery if p.id === id} yield p.status
    q.update(true)
    q.updateStatement
    findProductDetail(id)
  }

  object Actions {

    def findProductDetail(id: UUID): DBIO[Option[ProductDetail]] = for{
      productDetail <- QueryUtility.productDetailQuery.filter(_.id === id).result.headOption
    }yield productDetail

    def findProductDetailByProductId(productId: UUID): DBIO[Seq[ProductDetail]] = for{
      productDetails <- QueryUtility.productDetailQuery.filter(_.productId === productId).result
    }yield productDetails
  }
}
