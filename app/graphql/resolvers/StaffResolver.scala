package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import graphql.input.StaffInput
import models.{LoginUser, Role, Staff}
import services.{StaffService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class StaffResolver @Inject()(staffService: StaffService, userService: UserService, implicit val executionContext: ExecutionContext) {
  def createStaff(context: Context, staffInput: StaffInput): Future[Option[Staff]] = staffService.createStaff(context, staffInput)

  def login(username: String, password: String): Future[LoginUser] = staffService.login(username, password)

  def roles(context: Context): Future[List[Role]] = staffService.roles(context)

  def findAll(context: Context): Future[Seq[Staff]] = staffService.findAllStaff(context)

  def findStaffById(context: Context, staffId: String): Future[Option[Staff]] = staffService.findStaffById(context, UUID.fromString(staffId))

  def updateStaff(context: Context, staffId: String, fullName: String, phoneNumber: String
                  , address: String, noNik: String, dateOfBirth: Long, roleId: String, staffEmail: String, staffPassword: String): Future[Option[Staff]] = staffService.updateStaff(context, staffId, fullName, phoneNumber, address, noNik, dateOfBirth, roleId, staffEmail, staffPassword)

  def deleteStaff(context: Context, staffId: String): Future[Option[Staff]] = staffService.deleteStaff(context, staffId)

  def changePassword(context: Context, oldPassword: String, newPassword: String, confirmPassword: String): Future[Boolean] = staffService.changePassword(context, oldPassword, newPassword, confirmPassword)
}
