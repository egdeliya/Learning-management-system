package exceptions

case class CourseNotFoundException(message: String, cause: Throwable)
  extends Exception(message, cause)

