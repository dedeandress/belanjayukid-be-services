package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.repositoryInterfaces.StaffRepository
import repositories.StaffRepositoryImpl

class StaffModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[StaffRepository]).to(classOf[StaffRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
