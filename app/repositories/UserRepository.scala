package repositories
import models.User

import scala.concurrent.Future


trait UserRepository {

  import java.util.UUID

  def findAll(): Future[List[User]]

  def find(id: UUID): Future[Option[User]]

  def create(user: User): Future[User]

  def delete(id: UUID): Future[Boolean]

  def update(user: User): Future[User]

}
