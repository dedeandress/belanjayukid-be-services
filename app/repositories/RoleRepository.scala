package repositories

trait RoleRepository {

  import java.util.UUID

  import models.Role

  import scala.concurrent.Future

  def findAll(): Future[List[Role]]

  def findById(id: UUID): Future[List[Role]]
}
