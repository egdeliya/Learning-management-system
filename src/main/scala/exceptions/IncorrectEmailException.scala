package exceptions

case class IncorrectEmailException(message: String, cause: Throwable)
  extends Exception(message, cause)
