package Domain.Slick

import slick.jdbc.SQLiteProfile.api._

package object GroupToCourse {

  class GroupToCourse(tag: Tag)
    extends Table[(String, String)](tag, "GROUP_TO_COURSE") {

    def courseName: Rep[String] = column[String]("COURSE_ID")
    def groupName: Rep[String] = column[String]("GROUP_ID")

    def pk = primaryKey("pk_group_to_course", (courseName, groupName))
    def idx = index("idx_group_to_course", (courseName, groupName))
    def * = (courseName, groupName)
  }

  lazy val groupToCourse = TableQuery[GroupToCourse]
}
