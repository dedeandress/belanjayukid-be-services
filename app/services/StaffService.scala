package services

import java.util.UUID

import com.google.inject.Inject
import errors.{AlreadyExists, AuthorizationException, NotFound}
import graphql.Context
import graphql.input.StaffInput
import models._
import repositories.repositoryInterfaces.{RoleRepository, StaffRepository, UserProfileRepository, UserRepository}
import utilities.{BCryptUtility, JWTUtility}

import scala.concurrent.{ExecutionContext, Future}

class StaffService @Inject()(staffRepository: StaffRepository, userRepository: UserRepository
                             , userProfileRepository: UserProfileRepository, roleRepository: RoleRepository
                             , implicit val executionContext: ExecutionContext) {

  def createStaff(context: Context, staffInput: StaffInput): Future[Option[Staff]] = {
    val user = User(username = staffInput.userInput.username
      , password = BCryptUtility.hashPassword(staffInput.userInput.password), email = staffInput.userInput.email)
    val staffDetail = staffInput.userProfileInput
    for {
      searchUser <- userRepository.findUser(user.username)
      userId <- if (searchUser.isEmpty) userRepository.create(user)
      else throw AlreadyExists("username already exist")
      userProfile <- Future.successful(UserProfile(fullName = staffDetail.fullName, address = staffDetail.address
        , phoneNumber = staffDetail.phoneNumber, noNik = staffDetail.noNik, dateOfBirth = staffDetail.dateOfBirth
        , userId = userId))
      _ <- userProfileRepository.addUserProfile(userProfile)
      staff <- staffRepository.addStaff(Staff(userId = userId, roleId = UUID.fromString(staffInput.roleId)))
    } yield staff
  }

  def login(username: String, password: String): Future[LoginUser] = {
    userRepository.findUser(username).flatMap {
      user =>
        if (user.isEmpty) throw AuthorizationException("username or password doesn't exist")
        if (BCryptUtility.check(password, user.get.password)) {
          play.Logger.warn("user passed")
          staffRepository.findByUserId(user.get.id).flatMap {
            staff =>
              play.Logger.warn("staff found")
              roleRepository.findById(staff.get.roleId).map {
                role =>
                  LoginUser(JWTUtility.generateJWT(user.get, role.get.name), user.get.username, role.get.name)
              }
          }
        }
        else throw AuthorizationException("username or password doesn't exist")
    }
  }

  def roles(context: Context): Future[List[Role]] = {
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    roleRepository.findAll()
  }

  def findAllStaff(context: Context): Future[Seq[Staff]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    staffRepository.findAll()
  }

  def findStaffById(context: Context, staffId: UUID): Future[Option[Staff]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    staffRepository.findById(staffId)
  }

  def updateStaff(context: Context, staffId: String, fullName: String, phoneNumber: String
                  , address: String, noNik: String, dateOfBirth: Long, roleId: String, staffEmail: String): Future[Option[Staff]] ={
    if (!JWTUtility.isAdmin(context)) throw AuthorizationException("You are not authorized")
    staffRepository.findById(UUID.fromString(staffId)).flatMap{
      staff =>
        if (staff.nonEmpty) {
          userProfileRepository.findByUserId(staff.get.userId).flatMap {
            userProfile =>
              if (userProfile.nonEmpty) {
                val newUserProfile = UserProfile(
                  userId = staff.get.userId,
                  fullName = fullName,
                  phoneNumber = phoneNumber,
                  address = address,
                  noNik = noNik,
                  dateOfBirth = dateOfBirth,
                  id = userProfile.get.id
                )
                staffRepository.updateRoleAndEmail(staff.get.userId, UUID.fromString(roleId), staffEmail).flatMap {
                  updateRole =>
                    if (updateRole != 0) {
                      userProfileRepository.updateUserProfile(newUserProfile).flatMap {
                        _ =>
                          staffRepository.findByUserId(staff.get.userId)
                      }
                    }
                    else Future.failed(NotFound("Not Found"))
                }
              }
              else Future.failed(NotFound("Not Found"))
          }
        }
        else Future.failed(NotFound("Not Found"))
    }

  }
}
