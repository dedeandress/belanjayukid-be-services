package graphql.resolvers

import javax.inject.Inject
import repositories.UserProfileRepository
import java.util.UUID

import models.UserProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext

class UserProfileResolver @Inject()(userProfileRepository: UserProfileRepository, implicit val executionContext: ExecutionContext){

  def findUserProfile(id: UUID): Future[UserProfile] = userProfileRepository.findById(id)

}
