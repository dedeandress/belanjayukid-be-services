package services

import java.util.UUID

import com.google.inject.Inject
import models.{User, UserProfile}
import repositories.repositoryInterfaces.{RoleRepository, UserProfileRepository, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(roleRepository: RoleRepository, userProfileRepository: UserProfileRepository, userRepository: UserRepository, implicit val executionContext: ExecutionContext) {


  def users: Future[List[User]] = userRepository.findAll()

  def deleteUser(id: UUID): Future[Boolean] = userRepository.delete(id)

  def updateUser(id: UUID, username: String, password: String, email: String): Future[User] = userRepository.update(User(id, username, password, email))

  def findUser(id: UUID): Future[Option[User]] = userRepository.find(id)

  def findUserProfile(userId: UUID): Future[Option[UserProfile]] = userProfileRepository.findByUserId(userId)

}
