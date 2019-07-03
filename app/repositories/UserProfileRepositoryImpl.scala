package repositories

import com.google.inject.{Inject, Singleton}
import modules.AppDatabase

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

  override def findById(id: UUID): Future[UserProfile] = db.run(Actions.findById(id))

  object Actions{

    def findAll: DBIO[List[UserProfile]] = for{
      userProfile <- userProfileQuery.result
    }yield userProfile.toList

    def findById(id: UUID): DBIO[UserProfile] = for{
      userProfile <- userProfileQuery.filter(_.id===id).result.headOption
    }yield userProfile.get
  }
}
