package services

import com.google.inject.Inject
import errors.{AuthorizationException, NotFound}
import graphql.input.StaffInput
import models.{LoginUser, Staff, User, UserProfile}
import repositories.repositoryInterfaces.{RoleRepository, StaffRepository, UserProfileRepository, UserRepository}
import utilities.{BCryptUtility, JWTUtility}

import scala.concurrent.{ExecutionContext, Future}

class StaffService @Inject()(staffRepository: StaffRepository, userRepository: UserRepository
                             , userProfileRepository: UserProfileRepository, roleRepository: RoleRepository
                             , implicit val executionContext: ExecutionContext){

  def addStaff(staffInput: StaffInput): Future[Option[Staff]] = {
    val user = User(username = staffInput.userInput.username
      , password = BCryptUtility.hashPassword(staffInput.userInput.password), email = staffInput.userInput.email)
        val staffDetail = staffInput.userProfileInput
        for{
          userId <- userRepository.create(user)
          userProfile <- Future.successful(UserProfile(fullName = staffDetail.fullName, address = staffDetail.address
            , phoneNumber = staffDetail.phoneNumber, noNik = staffDetail.noNik, dateOfBirth = staffDetail.dateOfBirth
            , userId = userId))
          _ <- userProfileRepository.addUserProfile(userProfile)
          role <- roleRepository.findByName(staffInput.roleName)
          staff <- if(role.isEmpty) throw NotFound("Role Name is not found")
          else staffRepository.addStaff(Staff(userId = userId, roleId = role.get.id))
        }yield staff
    }

  def login(username: String, password: String): Future[LoginUser] = {
    userRepository.findUser(username).flatMap{
      user=>
        if(user.isEmpty) throw AuthorizationException("username or password doesn't exist")
        if (BCryptUtility.check(password, user.get.password)){
          play.Logger.warn(""+user.get.id)
          staffRepository.findByUserId(user.get.id).flatMap{
            staff=>
              play.Logger.warn(""+staff.get.id)
              roleRepository.findById(staff.get.roleId).map{
                role=>
                  LoginUser(JWTUtility.generateJWT(user.get, role.get.name), user.get.username, role.get.name)
              }
          }
        }
        else throw AuthorizationException("username or password doesn't exist")
    }
  }
}
