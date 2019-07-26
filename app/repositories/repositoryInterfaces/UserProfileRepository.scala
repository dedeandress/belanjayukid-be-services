package repositories.repositoryInterfaces

trait UserProfileRepository {

  import java.util.UUID

  import models.UserProfile

  import scala.concurrent.Future

  def findByUserId(userId: UUID): Future[Option[UserProfile]]

  def addUserProfile(userProfile: UserProfile) : Future[UserProfile]

  def findById(id: UUID): Future[Option[UserProfile]]
}
