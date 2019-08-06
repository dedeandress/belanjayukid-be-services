package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import models.Category
import services.CategoryService

import scala.concurrent.Future

class CategoryResolver @Inject()(categoryService: CategoryService){

  def category(context: Context, id: UUID): Future[Option[Category]] = categoryService.findCategory(context, id)

  def category(context: Context, category: Category): Future[Category] = categoryService.createCategory(context, category)

  def categories(context: Context): Future[Seq[Category]] = categoryService.getAllCategory(context)

}
