package modules

import com.google.inject._
import repositories.UserRepositoryImpl
import repositories.repositoryInterfaces.UserRepository

class UserModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl]).in(Scopes.SINGLETON)
  }
}