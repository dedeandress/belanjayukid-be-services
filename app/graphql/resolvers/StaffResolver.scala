package graphql.resolvers

import com.google.inject.Inject
import graphql.input.StaffInput
import models.{LoginUser, Role, Staff}
import services.{StaffService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class StaffResolver @Inject()(staffService: StaffService, userService: UserService, implicit val executionContext: ExecutionContext){
  def createStaff(staffInput: StaffInput): Future[Option[Staff]] = staffService.createStaff(staffInput)

  def login(username: String, password: String): Future[LoginUser] = staffService.login(username, password)

  def roles: Future[List[Role]] = staffService.roles

}
