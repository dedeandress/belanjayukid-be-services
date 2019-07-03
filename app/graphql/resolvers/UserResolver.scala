package graphql.resolvers

import com.google.inject.Inject
import repositories.UserRepository

import scala.concurrent.ExecutionContext

class UserResolver @Inject()(userRepository: UserRepository, implicit val executionContext: ExecutionContext){

  import java.util.UUID

  import models.User

  import scala.concurrent.Future

  def users: Future[List[User]] = userRepository.findAll()

  def addUser(username: String, password: String ,email: String, userProfileId: UUID): Future[User] = userRepository.create(User(username= username, password = password,email = email, userProfileId = userProfileId))

  def deleteUser(id: UUID): Future[Boolean] = userRepository.delete(id)

  def updateUser(id: UUID, username: String, password: String, email: String,userProfileId: UUID): Future[User] = userRepository.update(User(id, username, password, email, userProfileId))

  def findUser(id: UUID): Future[Option[User]] = userRepository.find(id)

}
