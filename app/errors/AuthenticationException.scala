package errors

import sangria.execution.UserFacingError

case class AuthenticationException(message: String) extends Exception with UserFacingError {
  override def getMessage: String = message
}
