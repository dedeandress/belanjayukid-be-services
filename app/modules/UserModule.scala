package modules

import com.google.inject._
import repositories.{UserRepository, UserRepositoryImpl}

class UserModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl]).in(Scopes.SINGLETON)
  }
}