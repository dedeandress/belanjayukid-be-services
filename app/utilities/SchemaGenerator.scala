package utilities

import models.Category.CategoryTable
import models.Customer.CustomerTable
import models.ProductDetail.ProductDetailTable
import models.Products.ProductsTable

import scala.reflect.io.File
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
import slick.driver.PostgresDriver.api._

object SchemaGenerator{

  def run = {
    println("---------------------GENERATING SCHEMA.....")

    val roles = TableQuery[RoleTable]
    val categories = TableQuery[CategoryTable]
    val users = TableQuery[UserTable]
    val userProfile = TableQuery[UserProfileTable]
    val sessions = TableQuery[SessionTable]
    val staffs = TableQuery[StaffTable]
    val customers = TableQuery[CustomerTable]
    val products = TableQuery[ProductsTable]
    val productDetail = TableQuery[ProductDetailTable]
    val suppliers = TableQuery[SupplierTable]
    val stores = TableQuery[StoreTable]
    val transactions = TableQuery[TransactionTable]
    val transactionDetail = TableQuery[TransactionDetailTable]
    val shipments = TableQuery[ShipmentTable]

    val file = File("/Users/dedeandres/Documents/Skripsi_Project_BE/api-belanjayuk.id/belanjayukid-be-services/conf/evolutions/default/1.sql")
    val sb = new StringBuilder("# --- !Ups  \n\n")
    roles.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    categories.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    users.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    userProfile.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    sessions.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    staffs.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    customers.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    products.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    productDetail.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    suppliers.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    stores.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    transactions.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    transactionDetail.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    shipments.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")

    sb.append("\n\n")
    sb.append("# --- !Downs")
    sb.append("\n\n")

    roles.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    categories.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    users.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    userProfile.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    sessions.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    staffs.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    customers.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    products.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    productDetail.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    suppliers.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    stores.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    transactions.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    transactionDetail.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    shipments.schema.drop.statements.foreach(st => sb.append(st.toString + ";\n"))
    file.writeAll(sb.toString)
    play.Logger.info("----------------------FINISHED GENERATING SCHEMA--------------------------")
  }
}
