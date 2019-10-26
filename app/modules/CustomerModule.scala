package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.CustomerRepositoryImpl
import repositories.repositoryInterfaces.CustomerRepository

class CustomerModule extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[CustomerRepository]).to(classOf[CustomerRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
