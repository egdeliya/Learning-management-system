package Domain.Slick.tables

import slick.jdbc.H2Profile.api._


package object Students {

  class Students(tag: Tag)
    extends Table[(Long, String, Long, String, String, String)](tag, "STUDENTS") {

    def id: Rep[Long] = column[Long]("ID", O.PrimaryKey)
    def group: Rep[String] = column[String]("GROUP")
    def entryYear: Rep[Long] = column[Long]("ENTRY_YEAR")
    def grade: Rep[String] = column[String]("GRADE")
    def form: Rep[String] = column[String]("FORM")
    def basis: Rep[String] = column[String]("BASIS")

    def idx = index("idx_students", id, unique = true)
    def * = (id, group, entryYear, grade, form, basis)
  }

  lazy val students = TableQuery[Students]
}
