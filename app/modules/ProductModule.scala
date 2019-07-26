package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.ProductsRepositoryImpl
import repositories.repositoryInterfaces.ProductsRepository

class ProductModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ProductsRepository]).to(classOf[ProductsRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
