package repositories

import java.util.UUID
import models.Role

import scala.concurrent.Future

trait RoleRepository {

  

  def findAll(): Future[List[Role]]

  def findById(id: UUID): Future[List[Role]]
}
