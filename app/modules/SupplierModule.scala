package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.SupplierRepositoryImpl
import repositories.repositoryInterfaces.SupplierRepository

class SupplierModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[SupplierRepository]).to(classOf[SupplierRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
