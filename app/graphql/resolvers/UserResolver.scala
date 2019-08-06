package graphql.resolvers

import com.google.inject.Inject
import services.UserService
import java.util.UUID

import graphql.input.UserInput
import models.User
import play.api.mvc.Headers

  import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class UserResolver @Inject()(implicit val executionContext: ExecutionContext, userService: UserService){

  def users: Future[List[User]] = userService.users

  def deleteUser(id: UUID): Future[Boolean] = userService.deleteUser(id)

  def updateUser(id: UUID, username: String, password: String, email: String): Future[User] = userService.updateUser(id,username, password, email)

  def user(id: UUID): Future[Option[User]] = userService.findUser(id)

}
