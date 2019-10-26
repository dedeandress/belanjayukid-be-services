package repositories

import java.util.UUID

import errors.NotFound
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

  override def findById(id: UUID): Future[Option[Staff]] = {
    play.Logger.warn(s"find staff id: $id")
    db.run(Action.findById(id))
  }

  override def findByUserId(userId: UUID): Future[Option[Staff]] = {
    play.Logger.warn(s"find staff id: $userId")
    db.run(Action.findByUserId(userId))
  }

  override def findAll(): Future[Seq[Staff]] = db.run(Action.findAll())

  override def updateRoleAndEmail(userId: UUID, roleId: UUID, email: String): Future[Int] = {
    play.Logger.warn(s"update role userid: $userId")
    db.run(Action.updateRole(userId, roleId))
    db.run(Action.updateEmail(userId, email))
  }

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

    def findAll(): DBIO[Seq[Staff]] = for {
      staff <- QueryUtility.staffQuery.result
    }yield staff

    def updateRole(userId: UUID, roleId: UUID): DBIO[Int] = for {
      update <- QueryUtility.staffQuery.filter(_.userId === userId).map(_.roleId).update(roleId)
      _ <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(())
      }
    }yield update

    def updateEmail(userId: UUID, email: String): DBIO[Int] = for {
      update <- QueryUtility.userQuery.filter(_.id === userId).map(_.email).update(email)
      _ <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(())
      }
    } yield update

  }

}
