package exceptions

case class InternalError(message: String, cause: Throwable)
  extends Exception(message, cause)
