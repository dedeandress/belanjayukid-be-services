package services

import java.util.UUID

import com.google.inject.Inject
import models.Category
import repositories.repositoryInterfaces.CategoryRepository

import scala.concurrent.Future

class CategoryService @Inject()(categoryRepository: CategoryRepository){

  def findCategory(id: UUID): Future[Option[Category]] = categoryRepository.findCategory(id)

}