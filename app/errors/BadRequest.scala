package errors

import sangria.execution.UserFacingError

case class BadRequest(msg: String) extends Exception(msg) with UserFacingError {
  override def getMessage(): String = msg
}
