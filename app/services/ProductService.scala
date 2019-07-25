package services

import com.google.inject.Inject
import models.Products
import repositories.repositoryInterfaces.ProductsRepository

import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject()(productsRepository: ProductsRepository, val executionContext: ExecutionContext){

  def addProduct(products: Products): Future[Products] ={
    
  }

}
