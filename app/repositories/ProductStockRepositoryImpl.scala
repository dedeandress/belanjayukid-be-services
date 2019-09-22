package repositories

import java.util.UUID

import com.google.inject.Inject
import models.ProductStock
import modules.AppDatabase
import repositories.repositoryInterfaces.ProductStockRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductStockRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends ProductStockRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def findProductStock(id: UUID): Future[Option[ProductStock]] = db.run(Actions.findProductStock(id))

  override def addProductStock(productStock: ProductStock): Future[ProductStock] = db.run(Actions.addProductStock(productStock))

  override def getAllProductStock: Future[Seq[ProductStock]] = db.run(Actions.getAllProductStock)

  override def deleteProductStock(id: UUID): Future[Int] = db.run(Actions.delete(id))

  object Actions {

    def addProductStock(productStock: ProductStock): DBIO[ProductStock] = for {
      id <- QueryUtility.productStockQuery returning QueryUtility.productStockQuery.map(_.id) += productStock
      productStock <- findProductStock(id)
    } yield productStock.get

    def findProductStock(id: UUID): DBIO[Option[ProductStock]] = for {
      productStock <- QueryUtility.productStockQuery.filter(_.id === id).result.headOption
    } yield productStock

    def getAllProductStock: DBIO[Seq[ProductStock]] = QueryUtility.productStockQuery.filter(_.status === true).result

    def delete(id: UUID): DBIO[Int] = for {
      update <- QueryUtility.productStockQuery.filter(_.id === id).map(_.status).update(false)
    } yield update
  }

}
