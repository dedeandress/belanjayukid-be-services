package repositories

import com.google.inject.{Inject, Singleton}
import modules.AppDatabase
import models.Role

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

@Singleton
class RoleRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends RoleRepository {

  val db = database.db
  val profile = database.profile

  import models.Role.RoleTable
  import profile.api._

  val roleQuery = TableQuery[RoleTable]

  override def findAll(): Future[List[Role]] = db.run(Actions.findAll)

  override def findById(id: Long): Future[List[Role]] = db.run(Actions.findById(id))

  object Actions{

    def findAll: DBIO[List[Role]] = for{
      roles <- roleQuery.result
    }yield roles.toList

    def findById(id: Long): DBIO[List[Role]] = for{
      roles <- roleQuery.filter(_.id===id).result
    }yield roles.toList
  }
}
