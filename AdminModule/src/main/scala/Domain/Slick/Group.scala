package Domain.Slick

import Domain.Models.Group
import slick.jdbc.H2Profile.api._

package object Groups {

  class Groups(tag: Tag)
    extends Table[Group](tag, "GROUPS") {

    def name: Rep[String] = column[String]("NAME", O.PrimaryKey)
    def faculty: Rep[String] = column[String]("FACULTY")
    def courseNum: Rep[Int] = column[Int]("COURSE_NUM")

    def idx = index("idx_groups", name, unique = true)
    def * = (name, faculty, courseNum) <> (Group.tupled, Group.unapply)
  }

  lazy val groups = TableQuery[Groups]
}

