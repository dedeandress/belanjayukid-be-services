package graphql.resolvers

import com.google.inject.Inject
import models.Role
import java.util.UUID

import repositories.repositoryInterfaces.RoleRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class RoleResolver @Inject()(roleRepository: RoleRepository, implicit val executionContext: ExecutionContext){


  def roles: Future[List[Role]] = roleRepository.findAll()

  def role(id: UUID): Future[Option[Role]] = roleRepository.findById(id)
}
