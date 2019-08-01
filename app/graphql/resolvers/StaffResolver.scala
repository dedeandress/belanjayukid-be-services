package graphql.resolvers

import com.google.inject.Inject
import graphql.Context
import graphql.input.StaffInput
import models.{LoginUser, Role, Staff}
import services.{StaffService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class StaffResolver @Inject()(staffService: StaffService, userService: UserService, implicit val executionContext: ExecutionContext){
  def createStaff(context: Context, staffInput: StaffInput): Future[Option[Staff]] = staffService.createStaff(context, staffInput)

  def login(username: String, password: String): Future[LoginUser] = staffService.login(username, password)

  def roles(context: Context): Future[List[Role]] = staffService.roles(context)

}
