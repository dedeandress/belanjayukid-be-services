package repositories

import java.util.UUID

import com.google.inject.Inject
import errors.NotFound
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

  override def updateProduct(products: Products): Future[Option[Products]] = db.run(Actions.updateProduct(products))

  override def deleteProduct(productsId: UUID): Future[Int] = db.run(Actions.update(productsId))

  object Actions {

    def findProduct(id: UUID): DBIO[Option[Products]] = for {
      product <- QueryUtility.productQuery.filter(_.id === id).result.headOption
    } yield product

    def addProduct(product: Products): DBIO[Products] = for {
      id <- QueryUtility.productQuery returning QueryUtility.productQuery.map(_.id) += product
      product <- findProduct(id)
    } yield product.get

    def updateProduct(products: Products): DBIO[Option[Products]] = for {
      update <- QueryUtility.productQuery.filter(_.id === products.id).update(products)
      _ <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(())
      }
      product <- findProduct(products.id)
    } yield product

    def update(id: UUID): DBIO[Int] = for {
      update <- QueryUtility.productQuery.filter(_.id === id).map(_.status).update(false)
    } yield update

  }
}