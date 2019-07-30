package graphql.resolvers

import com.google.inject.Inject
import graphql.input.StaffInput
import models.{LoginUser, Staff}
import services.{StaffService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class StaffResolver @Inject()(staffService: StaffService, userService: UserService, implicit val executionContext: ExecutionContext){
  def addStaff(staffInput: StaffInput): Future[Option[Staff]] = staffService.addStaff(staffInput)

  def login(username: String, password: String): Future[LoginUser] = staffService.login(username, password)

}
