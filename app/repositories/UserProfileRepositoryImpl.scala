package repositories

import com.google.inject.{Inject, Singleton}
import modules.AppDatabase
import play.api.Logger
import repositories.repositoryInterfaces.UserProfileRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserProfileRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends UserProfileRepository {

  val db = database.db
  val profile = database.profile

  import java.util.UUID

  import models.UserProfile
  import models.UserProfile.UserProfileTable
  import profile.api._

  val userProfileQuery = TableQuery[UserProfileTable]

  override def findByUserId(userId: UUID): Future[Option[UserProfile]] = db.run(Actions.findByUserId(userId))

  override def addUserProfile(userProfile: UserProfile): Future[UserProfile] = {
    play.Logger.warn("add UserProfile")
    db.run(Actions.addUserProfile(userProfile))
  }

  override def findById(id: UUID): Future[Option[UserProfile]] = db.run(Actions.findById(id))

  object Actions{

    def findAll: DBIO[List[UserProfile]] = for{
      userProfile <- userProfileQuery.result
    }yield userProfile.toList

    def findByUserId(userId: UUID): DBIO[Option[UserProfile]] = for{
      userProfile <- userProfileQuery.filter(_.userId===userId).result.headOption
    }yield userProfile

    def findById(id: UUID): DBIO[Option[UserProfile]] = for{
      userProfile <- userProfileQuery.filter(_.id===id).result.headOption
    }yield userProfile

    def addUserProfile(userProfile: UserProfile): DBIO[UserProfile] = for{
      id <- userProfileQuery returning userProfileQuery.map(_.id) += userProfile
      result <- findById(id)

    }yield result.get
  }
}
