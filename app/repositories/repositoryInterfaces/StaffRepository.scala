package repositories.repositoryInterfaces

import java.util.UUID

import models.Staff

import scala.concurrent.Future

trait StaffRepository {
  def addStaff(staff: Staff): Future[Option[Staff]]

  def findById(id: UUID): Future[Option[Staff]]

  def findByUserId(userId: UUID): Future[Option[Staff]]

  def findAll(): Future[Seq[Staff]]

  def updateRoleAndEmail(userId: UUID, roleId: UUID, email: String): Future[Int]

  def deleteStaff(staffId: UUID): Future[Option[Staff]]
}
