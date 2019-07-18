package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.{UserProfileRepository, UserProfileRepositoryImpl}

class UserProfileModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserProfileRepository]).to(classOf[UserProfileRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
