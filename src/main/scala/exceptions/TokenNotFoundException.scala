package exceptions

case class TokenNotFoundException(message: String, cause: Throwable)
  extends Exception(message, cause)
