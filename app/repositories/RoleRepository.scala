package repositories

trait RoleRepository {

  import models.Role

  import scala.concurrent.Future

  def findAll(): Future[List[Role]]

  def findById(id: Long): Future[List[Role]]
}
