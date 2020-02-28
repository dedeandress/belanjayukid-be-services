package graphql.resolvers

import com.google.inject.Inject
import graphql.Context
import models.Store
import services.StoreService

import scala.concurrent.Future

class StoreResolver @Inject()(storeService: StoreService){

  def store(context: Context): Future[Option[Store]] = storeService.fetchStore(context)

  def updateStore(context: Context, storeName: String, storePhoneNumber: String, storeAddress: String): Future[Option[Store]] = {
    storeService.updateStore(context, storeName, storePhoneNumber, storeAddress)
  }
}
