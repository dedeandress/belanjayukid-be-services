package graphql.resolvers

import java.util.UUID

import javax.inject.Inject
import models.UserProfile
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

class UserProfileResolver @Inject()(userService: UserService, implicit val executionContext: ExecutionContext){

  import graphql.input.UserProfileInput

  def findUserProfile(userId: UUID): Future[Option[UserProfile]] = userService.findUserProfile(userId)

  def insertUserProfile(userProfileInput: UserProfileInput): Future[UserProfile] = userService.insertUserProfile(userProfileInput)
}
