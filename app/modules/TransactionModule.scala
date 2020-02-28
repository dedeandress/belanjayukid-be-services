package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.TransactionRepositoryImpl
import repositories.repositoryInterfaces.TransactionRepository

class TransactionModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TransactionRepository]).to(classOf[TransactionRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
