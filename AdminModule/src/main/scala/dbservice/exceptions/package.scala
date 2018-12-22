package dbservice

package object exceptions {
  case class CourseNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class GroupNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class TeacherNotFoundException(message: String, cause: Throwable) extends Exception(message, cause)

  case class InternalError(message: String, cause: Throwable) extends Exception(message, cause)
}
