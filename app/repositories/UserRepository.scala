package repositories
import models.User

import scala.concurrent.Future


trait UserRepository {

  def findAll(): Future[List[User]]

  def find(id: Long): Future[Option[User]]

  def create(user: User): Future[User]

  def delete(id: Long): Future[Boolean]

  def update(user: User): Future[User]

}
