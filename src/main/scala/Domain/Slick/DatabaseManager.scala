package Domain.Slick

import Domain.Slick.tables.Auths.auths
import Domain.Slick.tables.Courses.courses
import Domain.Slick.tables.GroupToCourse.groupToCourse
import Domain.Slick.tables.Groups.groups
import Domain.Slick.tables.Students.students
import Domain.Slick.tables.TeacherToCourse.teacherToCourse
import Domain.Slick.tables.Teachers.teachers
import Domain.Slick.tables.Tokens.tokens
import Domain.Slick.tables.Users.users
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DatabaseManager(dbPath: String) {

  private lazy val db = initDb()
  private val tables = Map("courses" -> courses,
                            "groups" -> groups,
                            "groupToCourse" -> groupToCourse,
                            "teacherToCourse" -> teacherToCourse,
                            "users" -> users,
                            "teachers" -> teachers,
                            "students" -> students,
                            "tokens" -> tokens,
                            "auths" -> auths)

  def initDb() = Database.forConfig(dbPath)

  def exec[T](action: DBIO[T]): Future[T] = db.run(action)

  def close: Unit = {
    db.close()
  }

  def createTablesIfNotInExists(): Unit = {
    Await.ready(
      exec(MTable.getTables)
        .flatMap(tablesVector => {
          val tablesNames = tablesVector.map(mTable => mTable.name.name)
          val createIfNotExist = tables
            .values
            .filterNot(table => tablesNames.contains(table.baseTableRow.tableName))
            .map(_.schema.create)
          exec(DBIO.sequence(createIfNotExist))
        }),
      Duration.Inf
    )
  }

  def dropTables(): Unit = {
    val dropTables = tables.values.map(_.schema.drop)
    Await.ready(exec(DBIO.sequence(dropTables)), Duration.Inf)
  }
}
