package services

import java.util.UUID

import com.google.inject.Inject
import errors.AuthorizationException
import graphql.Context
import models.Category
import repositories.repositoryInterfaces.CategoryRepository
import utilities.JWTUtility

import scala.concurrent.Future

class CategoryService @Inject()(categoryRepository: CategoryRepository){

  def findCategory(context: Context, id: UUID): Future[Option[Category]] = {
    if(JWTUtility.isAdmin(context))categoryRepository.findCategory(id)
    else throw AuthorizationException("You are not authorized")
  }

  def createCategory(context: Context,category: Category): Future[Category] = {
    if(JWTUtility.isAdmin(context))categoryRepository.addCategory(category)
    else throw AuthorizationException("You are not authorized")
  }

  def getAllCategory(context: Context): Future[Seq[Category]] = {
    if(JWTUtility.isAdmin(context)) categoryRepository.getAllCategory
    else throw AuthorizationException("You are not authorized")
  }

}
