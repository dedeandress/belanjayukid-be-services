package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import graphql.input.UserProfileInput
import models.{Customer, User, UserProfile}
import repositories.repositoryInterfaces.{CustomerRepository, UserProfileRepository, UserRepository}
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}

class CustomerService @Inject()(customerRepository: CustomerRepository, userRepository: UserRepository, userProfileRepository: UserProfileRepository, implicit val executionContext: ExecutionContext) {

  def addCustomer(context: Context, userProfileInput: UserProfileInput): Future[Option[Customer]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val user = User()
    val userDetail = userProfileInput
    for {
      userId <- userRepository.create(user)
      userProfile <- Future.successful(UserProfile(fullName = userDetail.fullName, address = userDetail.address
        , phoneNumber = userDetail.phoneNumber, noNik = userDetail.noNik, dateOfBirth = userDetail.dateOfBirth
        , userId = userId))
      _ <- userProfileRepository.addUserProfile(userProfile)
      customer <- customerRepository.addCustomer(Customer(userId = userId))
    } yield customer
  }

  def findAllCustomer(context: Context): Future[Seq[Customer]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    customerRepository.findAll()
  }

  def findCustomerById(context: Context, customerId: UUID): Future[Option[Customer]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    customerRepository.findById(customerId)
  }

  def findCustomerByUserId(context: Context, userId: UUID): Future[Option[Customer]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    customerRepository.findByUserId(userId)
  }

  def deleteCustomer(context: Context, id: UUID): Future[Int] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    customerRepository.deleteCustomer(id)
  }

  def updateCustomer(context: Context, customerId: UUID, fullName: String, phoneNumber: String
                     , address: String, noNik: String, dateOfBirth: Long) : Future[Option[Customer]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    val update = for {
      customer <- customerRepository.findById(customerId)
      userProfile <- userProfileRepository.findByUserId(customer.get.userId)
      newUserProfile <- Future.successful(UserProfile(
      userId = customer.get.userId,
      fullName = fullName,
      phoneNumber = phoneNumber,
      address = address,
      noNik = noNik,
      dateOfBirth = dateOfBirth,
      id = userProfile.get.id
      ))
      updateUserProfile <- userProfileRepository.updateUserProfile(newUserProfile, "123")
    } yield updateUserProfile

    update.flatMap{
      _ => customerRepository.findById(customerId)
    }
  }
}
