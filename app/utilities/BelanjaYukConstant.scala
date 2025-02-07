package utilities

object BelanjaYukConstant {
  val secretKey = "B3L4NJAYUK.1D"
}

object TransactionStatus{
  val INITIAL = 0
  val ON_PROCESS = 1
  val ON_CHECKER = 2
  val COMPLETED = 3
  val ON_REFUND = 4
}

object PaymentStatus{
  val UNPAID = 0
  val PAID = 1
  val DEBT = 2
}

object TransactionDetailStatus{
  val EMPTY = 0
  val NOT_EMPTY = 1
  val COMPLETED = 2
  val CANCELED = 3
  val REFUNDED = 4
}

object PurchasesTransactionStatus{
  val INITIAL = 0
  val ON_PROCESS = 1
  val COMPLETED = 3
}
