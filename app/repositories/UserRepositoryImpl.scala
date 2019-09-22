package repositories

import java.util.UUID

import com.google.inject.{Inject, Singleton}
import errors.NotFound
import models.User
import modules.AppDatabase
import repositories.repositoryInterfaces.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepositoryImpl @Inject()(val database: AppDatabase, implicit val executionContext: ExecutionContext) extends UserRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  def userQuery = TableQuery[User.UserTable]

  override def findAll(): Future[List[User]] = db.run(Actions.findAll())

  override def create(user: User): Future[UUID] = {
    play.Logger.warn("add User")
    db.run(Actions.create(user))
  }

  override def find(id: UUID): Future[Option[User]] = db.run(Actions.find(id))

  override def update(user: User): Future[User] = db.run(Actions.update(user))

  override def delete(id: UUID): Future[Boolean] = db.run(Actions.delete(id))

  override def findUser(username: String): Future[Option[User]] = db.run(Actions.findUser(username))

  object Actions {
    def findAll(): DBIO[List[User]] = for {
      users <- userQuery.result
    } yield users.toList

    def create(user: User): DBIO[UUID] = for {
      userId <- userQuery returning userQuery.map(_.id) += user
    } yield userId

    def update(user: User): DBIO[User] = for {
      update <- userQuery.filter(_.id === user.id).update(user)
      _ <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(())
      }
      result <- find(user.id)
    } yield result.get

    def find(id: UUID): DBIO[Option[User]] = for {
      user <- userQuery.filter(_.id === id).result.headOption
    } yield user

    def delete(id: UUID): DBIO[Boolean] = for {
      maybeDelete <- userQuery.filter(_.id === id).delete
      isDelete = if (maybeDelete == 1) true else false
    } yield isDelete

    def findUser(username: String): DBIO[Option[User]] = for {
      user <- userQuery.filter(_.username === username).result.headOption
    } yield user
  }

}


