package graphql.`type`

import models.TransactionDetail

case class RefundTransactionResult(transactionDetails: Seq[TransactionDetail], totalRefund: BigDecimal, totalPrice: BigDecimal)
