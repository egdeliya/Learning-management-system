package Domain.Slick

import slick.jdbc.SQLiteProfile.api._

package object TeacherToCourse {

  class TeacherToCourse(tag: Tag)
    extends Table[(Int, String)](tag, "TEACHER_TO_COURSE") {

    def teacherId: Rep[Int] = column[Int]("TEACHER_ID")
    def courseName: Rep[String] = column[String]("COURSE_NAME")

    def pk = primaryKey("pk_teacher_to_course", (teacherId, courseName))
    def idx = index("idx_teacher_to_course", (teacherId, courseName))
    def * = (teacherId, courseName)
  }

  lazy val teacherToCourse = TableQuery[TeacherToCourse]
}
