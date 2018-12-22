import Domain.Models.{Course, Group, User}
import dbservice.{DatabaseManager, DatabaseServiceImpl}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AdminExample extends App {

  val dbManager = DatabaseManager

  dbManager.dropTables()
  dbManager.createTablesIfNotInExists()
  initApplication()
  dbManager.close

  def initApplication(): Unit = {
    val dbService = new DatabaseServiceImpl
    val adminService = new AdminServiceImpl(dbService)

    Await.ready(adminService
      .createGroup("hello", "hello", 34),
      Duration.Inf
    )

    Await.ready(adminService
      .createCourse("hello", "hello"),
      Duration.Inf
    )

    Await.ready(adminService
      .addGroupToCourse("hello", "hello"),
      Duration.Inf
    )

    Await.ready(adminService
      .addStudent("blame", "sur", "patr", "hello", 2343, "bachelor", "full-time", "contract"),
      Duration.Inf
    )

    Await.ready(adminService
      .addTeacher("name", "surname", "patr"),
      Duration.Inf
    )

    Await.ready(adminService
      .addTeacherToCourse(2, "hello"),
      Duration.Inf
    )
  }
}
