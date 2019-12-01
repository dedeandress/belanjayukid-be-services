package repositories.repositoryInterfaces

import java.util.UUID

import models.UserProfile

import scala.concurrent.Future

trait UserProfileRepository {

  def findByUserId(userId: UUID): Future[Option[UserProfile]]

  def addUserProfile(userProfile: UserProfile): Future[UserProfile]

  def findById(id: UUID): Future[Option[UserProfile]]

  def updateUserProfile(userProfile: UserProfile, password: String): Future[Int]
}
