package models

case class TransactionResult(status: Int, details: Seq[TransactionDetail], totalPrice: BigDecimal)
