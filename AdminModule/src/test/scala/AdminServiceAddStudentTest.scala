import Domain.Models._
import org.scalatest.{FlatSpec, Matchers}
import dbservice.{DatabaseManager, DatabaseServiceImpl}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait testStudentData extends testUsersData {

  private val groupNames = Seq("testGroup")
  private val degrees = List("bachelor")
  private val forms = List("full-time")
  private val basis = List("budget")
  private val entryYears = List(2017)

  val students: Seq[Student] = for {
    user <- users.take(3)
    groupName <- groupNames
    entryYear <- entryYears
    degree <- degrees
    form <- forms
    basis <- basis
  } yield Student(user,
    groupName,
    EntryYear(entryYear),
    Degree(degree),
    EducationForm(form),
    Basis(basis))
}

class AdminServiceAddStudentTest extends FlatSpec
  with Matchers
  with testStudentData
  with testGroupsData {

  trait init {

    val dbManager = new DatabaseManager("lmsdbtest")
    dbManager.dropTables()
    dbManager.createTablesIfNotInExists()

    private val dbService = new DatabaseServiceImpl(dbManager)
    val adminService = new AdminServiceImpl(dbService)

    def addStudents(): Future[Seq[(String, Int)]] = {
      Future.sequence(
        students.map { student =>
          adminService.addStudent(student.user.name,
            student.user.surname,
            student.user.patronymic,
            student.group,
            student.entryYear.value,
            student.degree.value,
            student.educationForm.value,
            student.basis.value
          )
        })
    }

    def createGroups(): Future[Seq[Unit]] = {
      Future.sequence(
        groups.map { group =>
          adminService.createGroup(group.groupName, group.facultyName, group.courseNum)
        })
    }

  }

  "AdminService" should "add student successfully" in new init {
    val result = createGroups()
      .flatMap(_ => addStudents())
      .transform(Success(_))
      .collect { case Success(_) => "success" }

    val res = Await.result(result, Duration.Inf)

    res shouldBe "success"
    dbManager.close
  }

  "AdminService" should "fail adding student to fake group" in new init {
    val fakeGroup = "fake"
    val result = Future.sequence(students.map { student =>
      adminService.addStudent(student.user.name,
        student.user.surname,
        student.user.patronymic,
        fakeGroup,
        student.entryYear.value,
        student.degree.value,
        student.educationForm.value,
        student.basis.value)
    }.map(_.transform(Success(_)))
    ).map(_.collect { case Failure(ex) => ex })

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe s"group $fakeGroup does not exist"
    dbManager.close
  }

  "AdminService" should "fail adding student with negative entry year" in new init {
    val wrongEntryYear = -2
    val result = createGroups()
        .flatMap { _ =>
          Future.sequence(students.map { student =>
            adminService.addStudent(student.user.name,
              student.user.surname,
              student.user.patronymic,
              student.group,
              wrongEntryYear,
              student.degree.value,
              student.educationForm.value,
              student.basis.value)
            }.map(_.transform(Success(_)))
          ).map(_.collect { case Failure(ex) => ex })
        }

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: entry year must be positive"
    dbManager.close
  }

  "AdminService" should "fail adding student with wrong degree" in new init {
    val wrongDegree = "blabla"
    val result = createGroups()
      .flatMap { _ =>
        Future.sequence(students.map { student =>
          adminService.addStudent(student.user.name,
            student.user.surname,
            student.user.patronymic,
            student.group,
            student.entryYear.value,
            wrongDegree,
            student.educationForm.value,
            student.basis.value)
        }.map(_.transform(Success(_)))
        ).map(_.collect { case Failure(ex) => ex })
      }

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: degree must be one of bachelor, specialist, magister"
    dbManager.close
  }

  "AdminService" should "fail adding student with wrong form" in new init {
    val wrongForm = "blabla"
    val result = createGroups()
      .flatMap { _ =>
        Future.sequence(students.map { student =>
          adminService.addStudent(student.user.name,
            student.user.surname,
            student.user.patronymic,
            student.group,
            student.entryYear.value,
            student.degree.value,
            wrongForm,
            student.basis.value)
        }.map(_.transform(Success(_)))
        ).map(_.collect { case Failure(ex) => ex })
      }

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: form must be one of full-time, part-time, evening"
    dbManager.close
  }

  "AdminService" should "fail adding student with wrong basis" in new init {
    val wrongBasis = "blabla"
    val result = createGroups()
      .flatMap { _ =>
        Future.sequence(students.map { student =>
          adminService.addStudent(student.user.name,
            student.user.surname,
            student.user.patronymic,
            student.group,
            student.entryYear.value,
            student.degree.value,
            student.educationForm.value,
            wrongBasis)
         }.map(_.transform(Success(_)))
        ).map(_.collect { case Failure(ex) => ex })
      }

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: basis must be one of contract, budget"
    dbManager.close
  }

}
