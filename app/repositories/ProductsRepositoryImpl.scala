package repositories

import java.util.UUID

import com.google.inject.Inject
import errors.NotFound
import graphql.`type`.ProductsResult
import models.{Products, TransactionDetail}
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

  override def findProduct(name: String): Future[Seq[Products]] = db.run(Actions.findProduct(name))

  override def getAllProductsWithPagination(limit: Int): Future[ProductsResult] = db.run(Actions.getAllProductsWithPagination(limit))

  override def getAllProducts: Future[Seq[Products]] = db.run(Actions.getAllProducts)

  override def updateStock(productId: UUID, stock: Int): Future[Int] = db.run(Actions.updateStock(productId, stock))

  override def decrementStock(details: Seq[TransactionDetail]): Unit = {
    val update = for (detail <- details) {

    }
  }

  object Actions {

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

    def findProduct(id: UUID): DBIO[Option[Products]] = for {
      product <- QueryUtility.productQuery.filter(_.id === id).result.headOption
    } yield product

    def update(id: UUID): DBIO[Int] = for {
      update <- QueryUtility.productQuery.filter(_.id === id).map(_.status).update(false)
    } yield update

    def findProduct(name: String): DBIO[Seq[Products]] = for {
      products <- QueryUtility.productQuery.filter(p => (p.name.toLowerCase like s"%${name.toLowerCase}%") && (p.status === true)).result
    } yield products

    def getAllProductsWithPagination(limit: Int): DBIO[ProductsResult] = for {
      products <- QueryUtility.productQuery.filter(_.status === true).sortBy(_.name.asc).take(limit).result
      numberOfProduct <- QueryUtility.productQuery.filter(_.status === true).length.result
    } yield ProductsResult(
      products = products,
      totalCount = numberOfProduct,
      hasNextData = numberOfProduct > limit
    )

    def getAllProducts: DBIO[Seq[Products]] = for {
      products <- QueryUtility.productQuery.filter(_.status === true).sortBy(_.name.asc).result
    } yield products

    def updateStock(productId: UUID, stock: Int): DBIO[Int] = for {
      update <- QueryUtility.productQuery.filter(_.id === productId).map(_.stock).update(stock)
    } yield update
  }

}