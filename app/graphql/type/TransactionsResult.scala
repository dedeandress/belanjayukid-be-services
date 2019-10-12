package graphql.`type`

import models.Transaction

case class TransactionsResult(transactions: Seq[Transaction], totalCount: Int, hasNextData: Boolean)
