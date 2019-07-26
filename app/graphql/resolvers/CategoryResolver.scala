package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.Category
import services.CategoryService

import scala.concurrent.Future

class CategoryResolver @Inject()(categoryService: CategoryService){

  def findCategory(id: UUID): Future[Option[Category]] = categoryService.findCategory(id)

}
