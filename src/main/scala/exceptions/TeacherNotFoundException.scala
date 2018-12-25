package exceptions

case class TeacherNotFoundException(message: String, cause: Throwable)
  extends Exception(message, cause)

