package repositories

import com.google.inject.{Inject, Singleton}
import modules.AppDatabase
import models.Role
import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import models.Role.RoleTable

@Singleton
class RoleRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends RoleRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  val roleQuery = TableQuery[RoleTable]

  override def findAll(): Future[List[Role]] = db.run(Actions.findAll)

  override def findById(id: UUID): Future[List[Role]] = db.run(Actions.findById(id))

  object Actions{


    def findAll: DBIO[List[Role]] = for{
      roles <- roleQuery.result
    }yield roles.toList

    def findById(id: UUID): DBIO[List[Role]] = for{
      roles <- roleQuery.filter(_.id===id).result
    }yield roles.toList
  }
}
