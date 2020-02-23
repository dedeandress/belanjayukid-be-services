package repositories

import java.util.UUID

import com.google.inject.Inject
import errors.NotFound
import models.Store
import modules.AppDatabase
import repositories.repositoryInterfaces.StoreRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class StoreRepositoryImpl @Inject()(val database: AppDatabase, implicit val executionContext: ExecutionContext) extends StoreRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def getStore: Future[Option[Store]] = {
    play.Logger.warn("get Store")
    db.run(Actions.getStore)
  }

  override def updateStore(store: Store): Future[Option[Store]] = {
    play.Logger.warn(s"update Store: $store")
    db.run(Actions.updateStore(store))
  }

  object Actions {

    def getStore(id: UUID): DBIO[Option[Store]] = for {
      store <- QueryUtility.storesQuery.filter(_.id === id).result.headOption
    } yield store

    def getStore: DBIO[Option[Store]] = for {
      store <- QueryUtility.storesQuery.result.headOption
    } yield store

    def updateStore(store: Store): DBIO[Option[Store]] = for {
      update <- QueryUtility.storesQuery.filter(_.id === store.id).update(store)
      findStore <- getStore(store.id)
      result <- update match {
        case 0 => DBIO.failed(NotFound("Store Not Found"))
        case _ => DBIO.successful(findStore)
      }
    } yield result
  }

}
