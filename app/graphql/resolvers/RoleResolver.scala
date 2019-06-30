package graphql.resolvers

import com.google.inject.Inject
import repositories.RoleRepository
import models.Role

import scala.concurrent.Future

import scala.concurrent.ExecutionContext

class RoleResolver @Inject()(roleRepository: RoleRepository, implicit val executionContext: ExecutionContext){

  def roles: Future[List[Role]] = roleRepository.findAll()

  def findRole(id: Long): Future[List[Role]] = roleRepository.findById(id)
}
