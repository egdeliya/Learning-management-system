package Domain.Slick.tables

import slick.jdbc.H2Profile.api._

package object Teachers {

  class Teachers(tag: Tag)
    extends Table[Long](tag, "TEACHERS") {

    def id: Rep[Long] = column[Long]("ID", O.PrimaryKey)

    def idx = index("idx_teachers", id, unique = true)
    def * = id
  }

  lazy val teachers = TableQuery[Teachers]
}
