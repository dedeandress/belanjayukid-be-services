package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.UserProfileRepositoryImpl
import repositories.repositoryInterfaces.UserProfileRepository

class UserProfileModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserProfileRepository]).to(classOf[UserProfileRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
