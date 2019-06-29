package repositories

import com.google.inject.{Inject, Singleton}
import models.User
import modules.AppDatabase

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepositoryImpl @Inject()(val database: AppDatabase, implicit val executionContext: ExecutionContext) extends UserRepository {

  val db = database.db
  val profile = database.profile

  import profile.api._

  def userQuery = TableQuery[User.Table]

  override def findAll(): Future[List[User]] = db.run(Actions.findAll())

  override def create(user: User): Future[User] = db.run(Actions.create(user))

  override def find(id: Long): Future[Option[User]] = db.run(Actions.find(id))

  override def update(user: User): Future[User] = db.run(Actions.update(user))

  override def delete(id: Long): Future[Boolean] = db.run(Actions.delete(id))

  object Actions {

    import errors.NotFound

    def find(id: Long): DBIO[Option[User]] = for {
      user <- userQuery.filter(_.id===id).result.headOption
    }yield user

    def findAll(): DBIO[List[User]] = for {
      users <- userQuery.result
    } yield users.toList

    def create(user: User): DBIO[User] = for{
        userId <- userQuery returning userQuery.map(_.id) += user
        result <- find(userId)
    }yield result.get

    def update(user: User): DBIO[User] = for{
      maybeId <- if(user.id.isDefined) DBIO.successful(user.id.get) else DBIO.failed(NotFound("not found user id"))
      update <- userQuery.filter(_.id===maybeId).update(user)
      _ <- update match {
        case 0 => DBIO.failed(NotFound("not found user id"))
        case _ => DBIO.successful(())
      }
      result <- find(maybeId)
    }yield result.get

    def delete(id: Long): DBIO[Boolean] = for {
      maybeDelete <- userQuery.filter(_.id === id).delete
      isDelete =  if(maybeDelete == 1) true else false
    }yield isDelete
  }

}


