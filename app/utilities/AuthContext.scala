package utilities

import errors.AuthenticationException
import models.User
import repositories.UserRepository

import scala.concurrent.ExecutionContext
import errors.AuthorizationException


case class AuthContext(userRepository: UserRepository, currentUser:Option[User] = None, implicit val executionContext: ExecutionContext) {

  import scala.concurrent.Future

  def login(username: String, password: String): Future[String] = {
    userRepository.findUser(username).map{
      user=>
        if(user.get.password==password){
        JWTUtility.generateJWT(user.get)
      }
        else throw AuthenticationException("email or password are incorrect")
    }
  }

  def ensureAuthenticated(): Unit = {
    if(currentUser.isEmpty) throw AuthorizationException("You do not have a permission. Please sign in")
  }
}
