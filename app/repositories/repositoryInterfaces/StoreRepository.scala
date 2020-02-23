package repositories.repositoryInterfaces

import models.Store

import scala.concurrent.Future

trait StoreRepository {

  def getStore: Future[Option[Store]]

  def updateStore(store: Store): Future[Option[Store]]

}
