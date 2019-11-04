package graphql.input

case class CheckTransactionInput(transactionId: String, transactionDetail: Seq[CheckTransactionDetailInput])
