package repositories

import java.util.UUID

import com.google.inject.Inject
import models.Products
import modules.AppDatabase
import repositories.repositoryInterfaces.ProductsRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class ProductsRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends ProductsRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addProduct(product: Products): Future[Products] = db.run(Actions.addProduct(product))

  override def findProduct(id: UUID): Future[Option[Products]] = db.run(Actions.findProduct(id))

  object Actions {

    def findProduct(id: UUID): DBIO[Option[Products]] = for{
      product <- QueryUtility.productQuery.filter(_.id === id).result.headOption
    }yield product

    def addProduct(product: Products) :DBIO[Products] = for {
      id <- QueryUtility.productQuery returning QueryUtility.productQuery.map(_.id) += product
      product <- findProduct(id)
    }yield product.get
  }
}