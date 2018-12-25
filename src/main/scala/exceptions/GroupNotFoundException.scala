package exceptions

case class GroupNotFoundException(message: String, cause: Throwable)
  extends Exception(message, cause)

