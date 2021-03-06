package Admin

import Admin.repo.AdminRepoImpl
import Domain.Models.{User, _}
import Domain.Slick.DatabaseManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait testData {
  private val groupNames = Seq("m05-1", "m05-2")
  private val courseNums = Seq(1, 2)
  private val faculties = Seq("diht")

  val groups: Seq[Group] = for {
    (groupName, courseNum) <- groupNames zip courseNums
    faculty <- faculties
  } yield Group(groupName, faculty, courseNum)

  private val courseNames = Seq("calculus", "programming")
  private val courseDescrs = Seq("base course", "the best course in the world")

  val courses: Seq[Course] = for {
    (courseName, courseDescr) <- courseNames.zip(courseDescrs)
  } yield Course(courseName, courseDescr)

  private val userNames = Seq("Alex", "Michael")
  private val userSurnames = Seq("Kulikov", "Melnikov")
  private val userPatronymics = Seq("Olegovich", "Dmitrievich")

  val users: Seq[User] = for {
    userName <- userNames
    surname <- userSurnames
    patronymic <- userPatronymics
  } yield User(None, userName, surname, patronymic)

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

object AdminExample extends App with testData {

  private val dbManager = new DatabaseManager("lmsdb")
  private val dbService = new AdminRepoImpl(dbManager)
  private val adminService = new AdminServiceImpl(dbService)

  dbManager.dropTables()
  dbManager.createTablesIfNotInExists()
  doAdminActions()
  dbManager.close

  def doAdminActions(): Unit = {
    val adminActions = createGroups()
      .zip(createCourses())
      .flatMap(_ => addGroupsToCourses())
      .flatMap(_ => addTeachers())
      .flatMap {
        teachersData =>
          println("\n\n Awailable tokens!")
          teachersData.foreach(tokenIdPair => println(tokenIdPair._1))
          addTeachersToCourses(teachersData.unzip._2)
      }
      .flatMap(_ => addStudents())
        .map {
          studentsData =>
            studentsData.foreach(tokenIdPair => println(tokenIdPair._1))
          println()
        }

    Await.result(adminActions, Duration.Inf)
  }

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

  def addGroupsToCourses(): Future[Seq[Unit]] = {
    Future.sequence( for {
        group <- groups.take(3)
        course <- courses.take(3)
      } yield adminService.addGroupToCourse(group.groupName, course.name)
    )
  }

  def addTeachers(): Future[Seq[(String, Long)]] = {
    Future.sequence(
      users.map { user =>
        adminService.addTeacher(user.name, user.surname, user.patronymic)
      })
  }

  def addTeachersToCourses(teachersIds: Seq[Long]): Future[Seq[Unit]] = {
    Future.sequence( for {
        teacherId <- teachersIds.take(3)
        course <- courses.take(3)
      } yield adminService.addTeacherToCourse(teacherId, course.name)
    )
  }

  def addStudents(): Future[Seq[(String, Long)]] = {
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

}
