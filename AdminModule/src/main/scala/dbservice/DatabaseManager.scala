package dbservice

import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import Domain.Slick.Courses.courses
import Domain.Slick.Groups.groups
import Domain.Slick.GroupToCourse.groupToCourse
import Domain.Slick.TeacherToCourse.teacherToCourse
import Domain.Slick.Users.users
import Domain.Slick.Teachers.teachers
import Domain.Slick.Students.students
import Domain.Slick.Tokens.tokens

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DatabaseManager(dbPath: String) {

  private lazy val db = initDb()
  private val tables = List(courses, groups, groupToCourse, teacherToCourse, users, teachers, students, tokens)

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
            .filterNot(table => tablesNames.contains(table.baseTableRow.tableName))
            .map(_.schema.create)
          exec(DBIO.sequence(createIfNotExist))
        }),
      Duration.Inf
    )
  }

  def dropTables(): Unit = {
    val dropTables = tables.map(_.schema.drop)
    Await.ready(exec(DBIO.sequence(dropTables)), Duration.Inf)
  }
}
