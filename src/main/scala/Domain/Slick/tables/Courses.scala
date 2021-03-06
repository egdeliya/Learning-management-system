package Domain.Slick.tables

import Domain.Models.Course
import slick.jdbc.H2Profile.api._

package object Courses {

  class Courses(tag: Tag)
    extends Table[Course](tag, "COURSES") {

    def name: Rep[String] = column[String]("NAME", O.PrimaryKey)
    def description: Rep[String] = column[String]("DESCRIPTION")

    def idx = index("idx_courses", name, unique = true)
    def * = (name, description) <> (Course.tupled, Course.unapply)
  }

  lazy val courses = TableQuery[Courses]
}
