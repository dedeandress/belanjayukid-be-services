package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import models.Supplier
import repositories.repositoryInterfaces.SupplierRepository
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}

class SupplierService @Inject()(supplierRepository: SupplierRepository, implicit val executionContext: ExecutionContext){

  def createSupplier(context: Context, name: String, phoneNumber: String, address: String): Future[Option[Supplier]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    supplierRepository.addSupplier(Supplier(name = name , phoneNumber = phoneNumber, address = address))
  }

  def supplier(context: Context, id: String): Future[Option[Supplier]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    supplierRepository.findById(UUID.fromString(id))
  }

  def suppliers(context: Context): Future[Seq[Supplier]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    supplierRepository.findAll()
  }

  def deleteSupplier(context: Context, id: String): Future[Int] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    supplierRepository.deleteSupplier(UUID.fromString(id))
  }

  def updateSupplier(context: Context, id: String, name: String, phoneNumber: String, address: String): Future[Option[Supplier]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    supplierRepository.updateSupplier(Supplier(id = UUID.fromString(id), name = name, phoneNumber = phoneNumber, address = address))
  }

}
