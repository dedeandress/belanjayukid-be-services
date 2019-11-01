package modules

import com.google.inject._
import repositories.PaymentRepositoryImpl
import repositories.repositoryInterfaces.PaymentRepository

class PaymentModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PaymentRepository]).to(classOf[PaymentRepositoryImpl]).in(Scopes.SINGLETON)
  }
}
