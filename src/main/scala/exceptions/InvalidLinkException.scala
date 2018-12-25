package exceptions

case class InvalidLinkException(message: String, cause: Throwable)
  extends Exception(message, cause)
