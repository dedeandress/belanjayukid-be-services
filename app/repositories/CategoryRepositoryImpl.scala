package repositories

import java.util.UUID

import com.google.inject.Inject
import models.Category
import modules.AppDatabase
import repositories.repositoryInterfaces.CategoryRepository
import utilities.QueryUtility

import scala.concurrent.{ExecutionContext, Future}

class CategoryRepositoryImpl @Inject()(database: AppDatabase, implicit val executionContext: ExecutionContext) extends CategoryRepository{

  val db = database.db
  val profile = database.profile

  import profile.api._

  override def addCategory(category: Category): Future[Category] = db.run(Actions.addCategory(category))

  override def findCategory(id: UUID): Future[Option[Category]] = db.run(Actions.findCategory(id))

  override def getAllCategory: Future[Seq[Category]] = db.run(Actions.getAllCategory)

  override def deleteCategory(id: UUID): Future[Int] = db.run(Actions.delete(id))

  object Actions{

    def findCategory(id: UUID): DBIO[Option[Category]] = for{
      category <- QueryUtility.categoryQuery.filter(_.id === id).result.headOption
    }yield category

    def addCategory(category: Category): DBIO[Category]= for{
      id <- QueryUtility.categoryQuery returning QueryUtility.categoryQuery.map(_.id) += category
      category <- findCategory(id)
    }yield category.get

    def getAllCategory: DBIO[Seq[Category]] = QueryUtility.categoryQuery.filter(_.status === true).result

    def delete(id: UUID) : DBIO[Int] = for {
      update <- QueryUtility.categoryQuery.filter(_.id === id).map(_.status).update(false)
    }yield update

  }
}
