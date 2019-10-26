package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import graphql.input.UserProfileInput
import models.Customer
import services.CustomerService

import scala.concurrent.{ExecutionContext, Future}

class CustomerResolver @Inject()(customerService: CustomerService, implicit val executionContext: ExecutionContext) {

  def customers(context: Context): Future[Seq[Customer]] = customerService.findAllCustomer(context)

  def addCustomer(context: Context, userProfileInput: UserProfileInput): Future[Option[Customer]] = customerService.addCustomer(context, userProfileInput)

  def updateCustomer(context: Context, customerId: String, fullName: String, phoneNumber: String
                     , address: String, noNik: String, dateOfBirth: Long): Future[Option[Customer]] = customerService.updateCustomer(context, UUID.fromString(customerId), fullName, phoneNumber, address, noNik, dateOfBirth)

  def deleteCustomer(context: Context, customerId: String): Future[Int] = customerService.deleteCustomer(context, UUID.fromString(customerId))

  def customer(context: Context, customerId: String): Future[Option[Customer]] = customerService.findCustomerById(context, UUID.fromString(customerId))
}
