package repositories

trait UserProfileRepository {

  import java.util.UUID

  import models.UserProfile

  import scala.concurrent.Future

  def findById(id: UUID): Future[UserProfile]
}
