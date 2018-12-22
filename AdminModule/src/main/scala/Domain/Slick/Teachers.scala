package Domain.Slick

import slick.jdbc.SQLiteProfile.api._


package object Teachers {

  class Teachers(tag: Tag)
    extends Table[Int](tag, "TEACHERS") {

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)

    def idx = index("idx_teachers", id, unique = true)
    def * = id
  }

  lazy val teachers = TableQuery[Teachers]
}

