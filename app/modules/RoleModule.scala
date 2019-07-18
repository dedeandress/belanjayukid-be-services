package modules

import com.google.inject._
import repositories.{RoleRepository, RoleRepositoryImpl}

class RoleModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[RoleRepository]).to(classOf[RoleRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
