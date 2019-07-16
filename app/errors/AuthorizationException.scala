package errors

case class AuthorizationException(message: String) extends Exception(message)
