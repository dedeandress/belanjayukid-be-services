package utilities

import scala.reflect.io.File

import slick.driver.PostgresDriver.api._

object SchemaGenerator{

  def run = {
    println("---------------------GENERATING SCHEMA.....")

    val file = File("/Users/andresd/Documents/Skripsi/API BelanjaYuk/belanjayukid-be-services/conf/evolutions/default/1.sql")
    val sb = new StringBuilder("# --- !Ups  \n\n")
    QueryUtility.roleQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.categoryQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.userQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.userProfileQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.sessionsQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.staffQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.customerQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.productQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.productStockQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.productDetailQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.suppliersQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.storesQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.paymentQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.transactionsQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.transactionDetailQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.shipmentsQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.purchasesTransactionQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.purchasesTransactionDetailQuery.schema.create.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")

    sb.append("\n\n")
    sb.append("# --- !Downs")
    sb.append("\n\n")

    QueryUtility.purchasesTransactionDetailQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.purchasesTransactionQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.suppliersQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.storesQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.transactionDetailQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.transactionsQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.sessionsQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.customerQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.userProfileQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.userQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.productDetailQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.productStockQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.staffQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.shipmentsQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.roleQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.categoryQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    QueryUtility.paymentQuery.schema.dropIfExists.statements.foreach(st => sb.append(st.toString + ";\n"))
    sb.append("\n\n")
    file.writeAll(sb.toString)
    play.Logger.info("----------------------FINISHED GENERATING SCHEMA--------------------------")
  }
}
