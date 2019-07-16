package errors

case class AuthenticationException(message: String) extends Exception(message)
