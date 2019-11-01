package modules

import com.google.inject.{AbstractModule, Scopes}
import repositories.PurchasesTransactionDetailRepositoryImpl
import repositories.repositoryInterfaces.PurchasesTransactionDetailRepository

class PurchasesTransactionDetailModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PurchasesTransactionDetailRepository]).to(classOf[PurchasesTransactionDetailRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
