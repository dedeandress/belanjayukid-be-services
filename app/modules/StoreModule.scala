package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.StoreRepositoryImpl
import repositories.repositoryInterfaces.StoreRepository

class StoreModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[StoreRepository]).to(classOf[StoreRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
