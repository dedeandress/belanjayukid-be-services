package graphql.resolvers

import com.google.inject.Inject
import graphql.Context
import models.Supplier
import services.SupplierService

import scala.concurrent.{ExecutionContext, Future}

class SupplierResolver @Inject()(supplierService: SupplierService, implicit val executionContext: ExecutionContext){

  def createSupplier(context: Context, name: String, phoneNumber: String, address: String): Future[Option[Supplier]] = {
    supplierService.createSupplier(context, name, phoneNumber, address)
  }

  def updateSupplier(context: Context, id: String, name: String, phoneNumber: String, address: String): Future[Option[Supplier]] = {
    supplierService.updateSupplier(context, id, name, phoneNumber, address)
  }

  def deleteSupplier(context: Context, id: String): Future[Int] = {
    supplierService.deleteSupplier(context, id)
  }

  def supplier(context: Context, id: String): Future[Option[Supplier]] = supplierService.supplier(context, id)

  def suppliers(context: Context): Future[Seq[Supplier]] = supplierService.suppliers(context)

}
