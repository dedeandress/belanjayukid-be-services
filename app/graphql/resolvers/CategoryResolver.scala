package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import models.Category
import services.CategoryService

import scala.concurrent.Future

class CategoryResolver @Inject()(categoryService: CategoryService){

  def category(id: UUID): Future[Option[Category]] = categoryService.findCategory(id)

  def category(category: Category): Future[Category] = categoryService.createCategory(category)

  def categories: Future[Seq[Category]] = categoryService.getAllCategory

}
