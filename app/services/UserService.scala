package services

import java.util.UUID

import com.google.inject.Inject
import models.User
import models.UserProfile
import repositories.{UserProfileRepository, UserRepository}
import graphql.input.{UserInput, UserProfileInput}
import com.auth0.jwt.interfaces.DecodedJWT

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class UserService @Inject()(userProfileRepository: UserProfileRepository, userRepository: UserRepository, implicit val executionContext: ExecutionContext){


//TODO
//  def registerUser(username: String, password: String ,email: String, userProfileId: UUID) ={
//    userResolver.addUser(new User(username = username, password = password, email = email))
//  }
//
//  def addUser(user: UserInput): Future[User] = {
//    userRepository.create(new User(username = user.username, password = user.password, email = user.email))
//  }
  import play.api.mvc.Headers
  import utilities.JWTUtility

  def login(username: String, password: String): Future[String] = {
    userRepository.findUser(username).map{
      user=>
        if(user.get.password==password)
        JWTUtility.generateJWT(user.get)
        else throw new Exception("not found")
    }
  }
  

  def isAuthorized(headers: Headers) = {
    import errors.Unauthorized
    if(headers.get("Authorization").isDefined) {
      val token = JWTUtility.extractToken(headers.get("Authorization").get).get
      val decodeJWT: DecodedJWT = JWTUtility.validateJWT(token)
      val id = decodeJWT.getClaim("id").asString()
      val user = userRepository.find(UUID.fromString(id))
      user
    }else throw new Unauthorized("you are not authorized")
  }

  def users: Future[List[User]] = userRepository.findAll()

//  def addUser(user: User): Future[User] = userRepository.create(user)

  def deleteUser(id: UUID): Future[Boolean] = userRepository.delete(id)

  def updateUser(id: UUID, username: String, password: String, email: String): Future[User] = userRepository.update(User(id, username, password, email))

  def findUser(id: UUID): Future[Option[User]] = userRepository.find(id)

  def findUserProfile(userId: UUID): Future[Option[UserProfile]] = userProfileRepository.findByUserId(userId)

//  def insertUserProfile(userProfileInput: UserProfileInput): Future[UserProfile] ={
//    userProfileRepository.addUserProfile(new UserProfile(fullName = userProfileInput.fullName, noNik = userProfileInput.noNik,
//      phoneNumber = userProfileInput.phoneNumber, address = userProfileInput.address, dateOfBirth = userProfileInput.dateOfBirth,
//      userId = UUID.fromString("00e33ac8-9d68-11e9-a2a3-2a2ae2dbcce4")))
//  }

}
