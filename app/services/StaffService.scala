package services

import com.google.inject.Inject
import errors.NotFound
import graphql.input.StaffInput
import models.{Staff, User, UserProfile}
import repositories.repositoryInterfaces.{RoleRepository, StaffRepository, UserProfileRepository, UserRepository}
import util.BCryptUtility

import scala.concurrent.{ExecutionContext, Future}

class StaffService @Inject()(staffRepository: StaffRepository, userRepository: UserRepository, userProfileRepository: UserProfileRepository, roleRepository: RoleRepository, implicit val executionContext: ExecutionContext){

  def addStaff(staffInput: StaffInput): Future[Option[Staff]] = {
    val user = User(username = staffInput.userInput.username, password = BCryptUtility.hashPassword(staffInput.userInput.password),
      email = staffInput.userInput.email)
    userRepository.create(user).flatMap{
      id =>
        val staffDetail = staffInput.userProfileInput
        val userProfile = UserProfile(fullName = staffDetail.fullName, address = staffDetail.address, phoneNumber = staffDetail.phoneNumber, noNik = staffDetail.noNik, dateOfBirth = staffDetail.dateOfBirth, userId = id)
        userProfileRepository.addUserProfile(userProfile)
        roleRepository.findByName(staffInput.roleName).flatMap{
          role=>
            if(role.isEmpty) throw NotFound("Role Name is not found")
            staffRepository.addStaff(Staff(userId = id, roleId = role.get.id))
        }
    }
  }
}
