package services

import com.google.inject.Inject
import errors.{AuthorizationException, NotFound}
import graphql.Context
import models.Store
import repositories.repositoryInterfaces.StoreRepository
import utilities.JWTUtility

import scala.concurrent.{ExecutionContext, Future}

class StoreService @Inject()(storeRepository: StoreRepository, implicit val executionContext: ExecutionContext){

  def fetchStore(context: Context): Future[Option[Store]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    storeRepository.getStore
  }

  def updateStore(context: Context, storeName: String, storePhoneNumber: String, storeAddress: String): Future[Option[Store]] = {
    if (!JWTUtility.isAdminOrCashier(context)) throw AuthorizationException("You are not authorized")
    storeRepository.getStore.flatMap{
      store =>
        if (store.nonEmpty) {
          storeRepository.updateStore(Store(id = store.get.id, name = storeName, phoneNumber= storePhoneNumber, address = storeAddress))
        } else Future.failed(NotFound("Store Not Found"))
    }
  }
}
