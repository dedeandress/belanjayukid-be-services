package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.TransactionDetailRepositoryImpl
import repositories.repositoryInterfaces.TransactionDetailRepository

class TransactionDetailModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TransactionDetailRepository]).to(classOf[TransactionDetailRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
