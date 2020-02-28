package repositories

import java.util.UUID

import com.google.inject.Inject
import models.{Customer, Staff}
import modules.AppDatabase
import repositories.repositoryInterfaces.CustomerRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class CustomerRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends CustomerRepository{
  val db = database.db
  val profile = database.profile

  import profile.api._
  override def addCustomer(customer: Customer): Future[Option[Customer]] = db.run(Actions.addCustomer(customer))

  override def findById(id: UUID): Future[Option[Customer]] = db.run(Actions.findById(id))

  override def findByUserId(userId: UUID): Future[Option[Customer]] = db.run(Actions.findByUserId(userId))

  override def findAll(): Future[Seq[Customer]] = db.run(Actions.findAll())

  override def deleteCustomer(id: UUID): Future[Int] = db.run(Actions.delete(id))

  object Actions{
    def findAll(): DBIO[Seq[Customer]] = for {
      customer <- QueryUtility.customerQuery.filter(_.status === true).result
    }yield customer

    def findByUserId(userId: UUID): DBIO[Option[Customer]] = for {
      customer <- QueryUtility.customerQuery.filter(_.userId === userId).result.headOption
    } yield customer

    def findById(id: UUID): DBIO[Option[Customer]] = for {
      customer <- QueryUtility.customerQuery.filter(_.id === id).result.headOption
    } yield customer

    def addCustomer(customer: Customer): DBIO[Option[Customer]] = for {
      id <- QueryUtility.customerQuery returning QueryUtility.customerQuery.map(_.id) += customer
      customer <- findById(id)
    } yield customer

    def delete(id: UUID) : DBIO[Int] = for {
      update <- QueryUtility.customerQuery.filter(_.id === id).map(_.status).update(false)
    }yield update
  }
}
