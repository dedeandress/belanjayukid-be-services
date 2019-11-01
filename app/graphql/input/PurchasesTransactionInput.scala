package graphql.input

case class PurchasesTransactionInput(purchasesTransactionId: String, supplierId: String, staffId: String, detail: List[PurchasesTransactionDetailInput])
