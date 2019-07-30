package repositories.repositoryInterfaces

import java.util.UUID

import models.Staff

import scala.concurrent.Future

trait StaffRepository {
  def addStaff(staff: Staff): Future[Option[Staff]]

  def findById(id: UUID): Future[Option[Staff]]

  def findByUserId(userId: UUID): Future[Option[Staff]]
}
