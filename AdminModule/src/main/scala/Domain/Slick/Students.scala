package Domain.Slick

import slick.jdbc.H2Profile.api._
//import Users.users

package object Students {

  class Students(tag: Tag)
    extends Table[(Int, String, Int, String, String, String)](tag, "STUDENTS") {

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)
    def group: Rep[String] = column[String]("GROUP")
    def entryYear: Rep[Int] = column[Int]("ENTRY_YEAR")
    def grade: Rep[String] = column[String]("GRADE")
    def form: Rep[String] = column[String]("FORM")
    def basis: Rep[String] = column[String]("BASIS")

    def idx = index("idx_students", id, unique = true)
    def * = (id, group, entryYear, grade, form, basis)
  }

  lazy val students = TableQuery[Students]
}

