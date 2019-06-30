package graphql.resolvers

import com.google.inject.Inject
import repositories.UserRepository

import scala.concurrent.ExecutionContext

class UserResolver @Inject()(userRepository: UserRepository, implicit val executionContext: ExecutionContext){

  import models.User

  import scala.concurrent.Future

  def users: Future[List[User]] = userRepository.findAll()

  def addUser(name: String, email: String, roleId: Long): Future[User] = userRepository.create(User(name= name, email = email, roleId = roleId))

  def deleteUser(id: Long): Future[Boolean] = userRepository.delete(id)

  def updateUser(id: Long, name: String, email: String, roleId: Long): Future[User] = userRepository.update(User(Some(id), name, email, roleId))

  def findUser(id: Long): Future[Option[User]] = userRepository.find(id)

}
