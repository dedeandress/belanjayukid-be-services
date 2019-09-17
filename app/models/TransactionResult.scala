package models

case class TransactionResult(status: Int, details: Seq[TransactionDetail])
