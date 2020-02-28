package graphql.input

case class TransactionInput(transactionId: String, customerId: String, staffId: String, detail: List[TransactionDetailInput])
