package modules

import com.google.inject._
import repositories.ProductStockRepositoryImpl
import repositories.repositoryInterfaces.ProductStockRepository

class ProductStockModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ProductStockRepository]).to(classOf[ProductStockRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
