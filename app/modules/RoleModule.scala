package modules

import com.google.inject._
import repositories.RoleRepositoryImpl
import repositories.repositoryInterfaces.RoleRepository

class RoleModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[RoleRepository]).to(classOf[RoleRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
