package graphql.resolvers

import java.util.UUID

import javax.inject.Inject
import models.UserProfile
import services.UserService

import scala.concurrent.{ExecutionContext, Future}
import graphql.input.UserProfileInput


class UserProfileResolver @Inject()(userService: UserService, implicit val executionContext: ExecutionContext){

  def findUserProfile(userId: UUID): Future[Option[UserProfile]] = userService.findUserProfile(userId)

//  def insertUserProfile(userProfileInput: UserProfileInput): Future[UserProfile] = userService.insertUserProfile(userProfileInput)
}
