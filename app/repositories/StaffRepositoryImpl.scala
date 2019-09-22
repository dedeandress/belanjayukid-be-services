package repositories

import java.util.UUID

import javax.inject.Inject
import models.Staff
import models.Staff.StaffTable
import modules.AppDatabase
import repositories.repositoryInterfaces.StaffRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class StaffRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends StaffRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  val staffQuery = TableQuery[StaffTable]

  override def addStaff(staff: Staff): Future[Option[Staff]] = {
    play.Logger.warn("add staff")
    db.run(Action.addStaff(staff))
  }

  override def findById(id: UUID): Future[Option[Staff]] = db.run(Action.findById(id))

  override def findByUserId(userId: UUID): Future[Option[Staff]] = db.run(Action.findByUserId(userId))

  object Action {

    def addStaff(staff: Staff): DBIO[Option[Staff]] = for {
      id <- staffQuery returning staffQuery.map(_.id) += staff
      staff <- findById(id)
    } yield staff

    def findById(id: UUID): DBIO[Option[Staff]] = for {
      staff <- staffQuery.filter(_.id === id).result.headOption
    } yield staff

    def findByUserId(userId: UUID): DBIO[Option[Staff]] = for {
      staff <- QueryUtility.staffQuery.filter(_.userId === userId).result.headOption
    } yield staff

  }

}
