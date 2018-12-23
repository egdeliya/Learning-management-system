import org.scalatest.{FlatSpec, Matchers}
import dbservice.{DatabaseManager, DatabaseServiceImpl}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class AdminServiceAddGroupToCourseTest extends FlatSpec
  with Matchers
  with testGroupsData
  with testCoursesData {

  trait init {
    val dbManager = new DatabaseManager("lmsdbtest")
    dbManager.dropTables()
    dbManager.createTablesIfNotInExists()

    private val dbService = new DatabaseServiceImpl(dbManager)
    val adminService = new AdminServiceImpl(dbService)

    def createGroups(): Future[Seq[Unit]] = {
      Future.sequence(
        groups.map { group =>
          adminService.createGroup(group.groupName, group.facultyName, group.courseNum)
        })
    }

    def createCourses(): Future[Seq[Unit]] = {
      Future.sequence(
        courses.map { course =>
          adminService.createCourse(course.name, course.description)
        })
    }
  }

  "AdminService" should "add group to course successfully" in new init {
    val result = createCourses()
      .zip(createGroups())
      .flatMap { _ =>
        adminService.addGroupToCourse(groups.head.groupName, courses.head.name)
      }.transform(Success(_))
      .collect{ case Success(_) => "success" }

    val res = Await.result(result, Duration.Inf)

    res shouldBe "success"
    dbManager.close
  }

  "AdminService" should "fail adding fake group to course" in new init {
    val fakeGroup = "helpme"
    val result = createCourses()
      .flatMap { _ =>
        adminService.addGroupToCourse(fakeGroup, courses.head.name)
      }.transform(Success(_))
      .collect{ case Failure(ex) => ex }

    val exceptions = Await.result(result, Duration.Inf)

    exceptions.getMessage shouldBe s"group $fakeGroup does not exist"
    dbManager.close
  }

  "AdminService" should "fail adding group to fake course" in new init {
    val fakeCourse = "helpme"
    val result = createGroups()
      .flatMap { _ =>
        adminService.addGroupToCourse(groups.head.groupName, fakeCourse)
      }.transform(Success(_))
      .collect{ case Failure(ex) => ex }

    val exceptions = Await.result(result, Duration.Inf)

    exceptions.getMessage shouldBe s"course $fakeCourse does not exist"
    dbManager.close
  }

  "AdminService" should "fail adding fake group to fake course" in new init {
    val fakeCourse = "helpme"
    val fakeGroup = "please"
    val result = adminService.addGroupToCourse(fakeGroup, fakeCourse)
      .transform(Success(_))
      .collect{ case Failure(ex) => ex }

    val exceptions = Await.result(result, Duration.Inf)

    exceptions.getMessage shouldBe s"no course $fakeCourse and group $fakeGroup"
    dbManager.close
  }
}
