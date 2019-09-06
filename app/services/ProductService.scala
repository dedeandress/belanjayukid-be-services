package services

import java.util.UUID

import com.google.inject.Inject
import graphql.input.ProductInput
import models.{ProductDetail, Products}
import repositories.repositoryInterfaces.{CategoryRepository, ProductDetailRepository, ProductStockRepository, ProductsRepository}

import scala.concurrent.{ExecutionContext, Future}

class ProductService @Inject()(productsRepository: ProductsRepository, categoryRepository: CategoryRepository, productDetailRepository: ProductDetailRepository, productStockRepository: ProductStockRepository, implicit val executionContext: ExecutionContext){

  def findProduct(id: UUID): Future[Option[Products]] = productsRepository.findProduct(id)

  def addProductDetail(product: Products, productInput: ProductInput): Future[Unit] = {
    Future.successful(
      for(productDetail <- productInput.productDetailInput) {
        productDetailRepository.addProductDetail(
          new ProductDetail(
            value = productDetail.value,
            purchasePrice = productDetail.purchasePrice,
            sellingPrice = productDetail.sellingPrice,
            productId = product.id,
            productStockId = UUID.fromString(productDetail.productStockId)
          )
        )
      }
    )
  }

  def addProduct(productInput: ProductInput): Future[Products] ={
    for {
      product <- productsRepository.addProduct(new Products(SKU = productInput.SKU, name = productInput.name, categoryId = UUID.fromString(productInput.categoryId)))
      productDetail <- addProductDetail(product, productInput)
    }yield product

//      productsRepository.addProduct(new Products(SKU = productInput.SKU, name = productInput.name, categoryId = UUID.fromString(productInput.categoryId))).map{
//        product=>
//          for(productDetail <- productInput.productDetailInput){
//            productDetailRepository.addProductDetail(
//              new ProductDetail(
//                value = productDetail.value,
//                purchasePrice = productDetail.purchasePrice,
//                sellingPrice = productDetail.sellingPrice,
//                productId = product.id,
//                productStockId = UUID.fromString(productDetail.productStockId)
//              )
//            )
//          }
//      }
  }

  def updateProduct(productId: UUID, categoryId: UUID, name: String): Future[Option[Products]] = {
    productsRepository.findProduct(productId).flatMap{
      product =>
        productsRepository.updateProduct(product.get.copy(categoryId = categoryId, name = name))
    }

  }


}
