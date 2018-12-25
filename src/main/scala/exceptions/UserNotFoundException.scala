package exceptions

case class UserNotFoundException(message: String, cause: Throwable)
  extends Exception(message, cause)
