package graphql.resolvers

import java.util.UUID

import com.google.inject.Inject
import graphql.Context
import models.ProductStock
import services.ProductStockService

import scala.concurrent.Future

class ProductStockResolver @Inject()(productStockService: ProductStockService){

  def productStock(context: Context, id: UUID): Future[Option[ProductStock]] = productStockService.findProductStock(context, id)

  def productStocks(context: Context): Future[Seq[ProductStock]] = productStockService.getAllProductStock(context)
  
  def createProductStock(context: Context, productStock: ProductStock): Future[ProductStock] = productStockService.createProductStock(context, productStock)

  def deleteProductStock(context: Context, id: UUID): Future[Int] = productStockService.deleteProductStock(context, id)

}
