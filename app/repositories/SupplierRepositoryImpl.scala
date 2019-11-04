package repositories

import java.util.UUID

import errors.NotFound
import javax.inject.Inject
import models.Supplier
import modules.AppDatabase
import repositories.repositoryInterfaces.SupplierRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class SupplierRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends SupplierRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addSupplier(supplier: Supplier): Future[Option[Supplier]] = db.run(Actions.addSupplier(supplier))

  override def deleteSupplier(supplierId: UUID): Future[Int] = db.run(Actions.delete(supplierId))

  override def updateSupplier(supplier: Supplier): Future[Option[Supplier]] = db.run(Actions.update(supplier))

  override def findAll(): Future[Seq[Supplier]] = db.run(Actions.getAllSupplier)

  override def findById(id: UUID): Future[Option[Supplier]] = db.run(Actions.findSupplier(id))

  object Actions {

    def findSupplier(id: UUID): DBIO[Option[Supplier]] = for{
      supplier <- QueryUtility.suppliersQuery.filter(_.id === id).result.headOption
    }yield supplier

    def addSupplier(supplier: Supplier): DBIO[Option[Supplier]] = for{
      id <- QueryUtility.suppliersQuery returning QueryUtility.suppliersQuery.map(_.id) += supplier
      supplier <- findSupplier(id)
    }yield supplier

    def getAllSupplier: DBIO[Seq[Supplier]] = QueryUtility.suppliersQuery.filter(_.status === true).result

    def delete(id: UUID) : DBIO[Int] = for {
      update <- QueryUtility.suppliersQuery.filter(_.id === id).map(_.status).update(false)
    }yield update

    def update(supplier: Supplier): DBIO[Option[Supplier]] = for {
      update <- QueryUtility.suppliersQuery.filter(_.id === supplier.id).update(supplier)
      findSupplier <- findSupplier(supplier.id)
      supplier <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(findSupplier)
      }
    }yield supplier

  }

}
