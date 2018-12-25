package Admin

import Admin.repo.AdminRepoImpl
import Domain.Slick.DatabaseManager
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class AdminServiceAddTeacherToCourseTest extends FlatSpec
  with Matchers
  with testCoursesData
  with testUsersData {

  trait init {

    val dbManager = new DatabaseManager("lmsdbtest")
    dbManager.dropTables()
    dbManager.createTablesIfNotInExists()

    private val dbService = new AdminRepoImpl(dbManager)
    val adminService = new AdminServiceImpl(dbService)

    def createCourses(): Future[Seq[Unit]] = {
      Future.sequence(
        courses.map { course =>
          adminService.createCourse(course.name, course.description)
        })
    }

    def addTeachers(): Future[Seq[(String, Long)]] = {
      Future.sequence(
        users.map { user =>
          adminService.addTeacher(user.name, user.surname, user.patronymic)
        })
    }
  }

  "Admin.AdminService" should "add teacher to course successfully" in new init {
    val result = createCourses()
      .flatMap(_ => addTeachers())
      .flatMap {teachersIds =>
        adminService.addTeacherToCourse(teachersIds.head._2, courses.head.name)
      }.transform(Success(_))
      .collect{ case Success(_) => "success" }

    val res = Await.result(result, Duration.Inf)

    res shouldBe "success"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding fake teacher to course" in new init {
    val fakeTeacherId = 0
    val result = createCourses()
      .flatMap {_ =>
        adminService.addTeacherToCourse(fakeTeacherId, courses.head.name)
      }.transform(Success(_))
      .collect { case Failure(ex) => ex }

    val exception = Await.result(result, Duration.Inf)

    exception.getMessage shouldBe s"teacher with id $fakeTeacherId does not exist"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding teacher to fake course" in new init {
    val fakeCourse = "bla"
    val result = addTeachers()
      .flatMap {teachersIds =>
        adminService.addTeacherToCourse(teachersIds.head._2, fakeCourse)
      }.transform(Success(_))
      .collect { case Failure(ex) => ex }

    val exception = Await.result(result, Duration.Inf)

    exception.getMessage shouldBe s"course $fakeCourse does not exist"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding fake teacher to fake course" in new init {
    val fakeCourse = "bla"
    val fakeTeacherId = 0
    val result = adminService.addTeacherToCourse(fakeTeacherId, fakeCourse)
      .transform(Success(_))
      .collect { case Failure(ex) => ex }

    val exception = Await.result(result, Duration.Inf)

    exception.getMessage shouldBe s"no course $fakeCourse and teacher with id $fakeTeacherId"
    dbManager.close
  }
}
