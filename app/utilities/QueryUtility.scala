package utilities

import models.Category.CategoryTable
import models.Customer.CustomerTable
import models.ProductDetail.ProductDetailTable
import models.ProductStock.ProductStockTable
import models.Products.ProductsTable
import models.Role.RoleTable
import models.Session.SessionTable
import models.Shipment.ShipmentTable
import models.Staff.StaffTable
import models.Store.StoreTable
import models.Supplier.SupplierTable
import models.Transaction.TransactionTable
import models.TransactionDetail.TransactionDetailTable
import models.User.UserTable
import models.UserProfile.UserProfileTable
import slick.lifted.TableQuery

object QueryUtility {
  val categoryQuery = TableQuery[CategoryTable]
  val roleQuery = TableQuery[RoleTable]
  val userQuery = TableQuery[UserTable]
  val userProfileQuery = TableQuery[UserProfileTable]
  val sessionsQuery = TableQuery[SessionTable]
  val staffQuery = TableQuery[StaffTable]
  val customerQuery = TableQuery[CustomerTable]
  val productQuery = TableQuery[ProductsTable]
  val productStockQuery = TableQuery[ProductStockTable]
  val productDetailQuery = TableQuery[ProductDetailTable]
  val suppliersQuery = TableQuery[SupplierTable]
  val storesQuery = TableQuery[StoreTable]
  val transactionsQuery = TableQuery[TransactionTable]
  val transactionDetailQuery = TableQuery[TransactionDetailTable]
  val shipmentsQuery = TableQuery[ShipmentTable]
}
