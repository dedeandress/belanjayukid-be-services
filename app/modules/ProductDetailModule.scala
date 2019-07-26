package modules

import com.google.inject._
import repositories.ProductDetailRepositoryImpl
import repositories.repositoryInterfaces.ProductDetailRepository

class ProductDetailModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ProductDetailRepository]).to(classOf[ProductDetailRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
