package errors

import sangria.execution.UserFacingError

case class AuthorizationException(msg: String) extends Exception(msg) with UserFacingError {
  override def getMessage(): String = msg
}