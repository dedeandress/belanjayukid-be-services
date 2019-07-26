package repositories.repositoryInterfaces

import java.util.UUID

import models.Category

import scala.concurrent.Future

trait CategoryRepository {

  def addCategory(category: Category): Future[Category]

  def findCategory(id: UUID): Future[Option[Category]]

}
