package repositories

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import models.Role
import models.Role.RoleTable
import modules.AppDatabase
import repositories.repositoryInterfaces.RoleRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoleRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends RoleRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  val roleQuery = TableQuery[RoleTable]

  override def findAll(): Future[List[Role]] = db.run(Actions.findAll)

  override def findById(id: UUID): Future[Option[Role]] = {
    play.Logger.warn("search role")
    db.run(Actions.findById(id))
  }

  override def findByName(roleName: String): Future[Option[Role]] = db.run(Actions.findByName(roleName))

  object Actions {

    def findAll: DBIO[List[Role]] = for {
      roles <- roleQuery.result
    } yield roles.toList

    def findById(id: UUID): DBIO[Option[Role]] = for {
      roles <- roleQuery.filter(_.id === id).result.headOption
    } yield roles

    def findByName(roleName: String): DBIO[Option[Role]] = for {
      role <- roleQuery.filter(_.name === roleName).result.headOption
    } yield role
  }

}
