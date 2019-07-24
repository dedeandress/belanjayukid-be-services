package graphql.resolvers

import com.google.inject.Inject
import graphql.input.StaffInput
import models.Staff
import services.StaffService

import scala.concurrent.{ExecutionContext, Future}

class StaffResolver @Inject()(staffService: StaffService, implicit val executionContext: ExecutionContext){
  def addStaff(staffInput: StaffInput): Future[Option[Staff]] = staffService.addStaff(staffInput)
}
