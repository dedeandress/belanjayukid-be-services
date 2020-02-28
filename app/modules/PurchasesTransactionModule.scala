package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.PurchasesTransactionRepositoryImpl
import repositories.repositoryInterfaces.PurchasesTransactionRepository

class PurchasesTransactionModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PurchasesTransactionRepository]).to(classOf[PurchasesTransactionRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
