package graphql.resolvers

import com.google.inject.Inject
import services.UserService

import scala.concurrent.ExecutionContext

class UserResolver @Inject()(implicit val executionContext: ExecutionContext, userService: UserService){

  import java.util.UUID

  import graphql.input.UserInput
  import models.User
  import play.api.mvc.Headers

  import scala.concurrent.Future

  def users: Future[List[User]] = userService.users

  def addUser(user: User): Future[User] = userService.addUser(user)

  def deleteUser(id: UUID): Future[Boolean] = userService.deleteUser(id)

  def updateUser(id: UUID, username: String, password: String, email: String): Future[User] = userService.updateUser(id,username, password, email)

  def findUser(id: UUID): Future[Option[User]] = userService.findUser(id)

  def login(username: String, password: String): Future[String] = userService.login(username, password)

  def inputUser(user: UserInput): Future[User] ={
    userService.addUser(user)
  }

  def getUser(headers: Headers): Future[Option[User]] = {
    userService.isAuthorized(headers)
  }

}
