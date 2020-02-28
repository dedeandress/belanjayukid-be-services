package models

case class TransactionResult(status: Int, details: Seq[TransactionDetail], totalPrice: BigDecimal, debt: BigDecimal = 0)
