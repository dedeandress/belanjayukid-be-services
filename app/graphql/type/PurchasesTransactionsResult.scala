package graphql.`type`

import models.PurchasesTransactionDetail

case class PurchasesTransactionsResult(status: Int, details: Seq[PurchasesTransactionDetail], totalPrice: BigDecimal, debt: BigDecimal = 0)
