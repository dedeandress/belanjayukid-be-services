package modules

import com.google.inject._
import repositories.repositoryInterfaces.CategoryRepository
import repositories.CategoryRepositoryImpl

class CategoryModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CategoryRepository]).to(classOf[CategoryRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
