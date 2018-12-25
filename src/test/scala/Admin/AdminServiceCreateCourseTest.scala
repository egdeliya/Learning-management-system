package Admin

import Admin.repo.AdminRepoImpl
import Domain.Models.Course
import Domain.Slick.DatabaseManager
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

trait testCoursesData {
  private val courseNames = Seq("testCourse")
  private val courseDescrs = Seq("base course")

  val courses: Seq[Course] = for {
    (courseName, courseDescr) <- courseNames.zip(courseDescrs)
  } yield Course(courseName, courseDescr)
}

class AdminServiceCreateCourseTest extends FlatSpec
  with Matchers
  with testCoursesData {

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
  }

  "Admin.AdminService" should "create course successfully" in new init {
    val result = Future.sequence(courses.map { course =>
      adminService.createCourse(course.name, course.description)
    }.map(_.transform(Success(_)))
    ).map(_.collect { case Success(_) => "success" })

    val res = Await.result(result, Duration.Inf)

    res should have size 1
    res.foreach(res => res shouldBe "success")
    dbManager.close
  }

  "Admin.AdminService" should "fail creating course with empty name" in new init {
    val result = Future.sequence(courses.map { course =>
      adminService.createCourse("", course.description)
    }.map(_.transform(Success(_)))
    ).map(_.collect{ case Failure(x) => x })

    val exceptions = Await.result(result, Duration.Inf)

    exceptions should have size 1
    exceptions.head.getMessage shouldBe "requirement failed: course name must be non empty!"
    dbManager.close
  }

  "Admin.AdminService" should "fail creating course with empty description" in new init {
    val result = Future.sequence(courses.map { course =>
      adminService.createCourse(course.name, "")
    }.map(_.transform(Success(_)))
    ).map(_.collect{ case Failure(x) => x })

    val exceptions = Await.result(result, Duration.Inf)

    exceptions should have size 1
    exceptions.head.getMessage shouldBe "requirement failed: course description must be non empty!"
    dbManager.close
  }
}
