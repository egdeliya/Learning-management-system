import Domain.Models._
import dbservice.DatabaseService

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

class AdminServiceImpl(private val dbService: DatabaseService)
  extends AdminService {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def createGroup(groupName: String,
                  facultyName: String,
                  courseNum: Int): Future[Unit] = {
    Try {
      Group(groupName, facultyName, courseNum)
    }.map { group =>
      dbService.createGroup(group)
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.debug(s"createGroup: failed creating group ${groupName} with parameters ${facultyName}, ${courseNum}")
        Future.failed(ex)
    }
  }

  def createCourse(courseName: String,
                   courseDescription: String): Future[Unit] = {
    Try {
      Course(courseName, courseDescription)
    }.map { course =>
      dbService.createCourse(course)
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"createCourse: failed creating course ${courseName} with description ${courseDescription}")
        Future.failed(ex)
    }
  }

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit] = {
    dbService.addGroupToCourse(groupName, courseName)
  }

  def addTeacherToCourse(teacherId: Int,
                         courseName: String): Future[Unit] = {
    dbService.addTeacherToCourse(teacherId, courseName)
  }

  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Int,
                 grade: String,
                 form: String,
                 basis: String): Future[(String, Int)] = {
    Try {
      val user = User(None, name, surname, patronymic)
      Student(user,
              group,
              EntryYear(entryYear),
              Degree(grade),
              EducationForm(form),
              Basis(basis))

    }.map { student =>
      dbService.addStudent(student)
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"addStudent: failed adding Student")
        Future.failed(ex)
    }
  }

  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[(String, Int)] = {
    Try {
      User(None, name, surname, patronymic)
    }.map { user =>
      dbService.addTeacher(Teacher(user))
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"addTeacher: failed adding teacher")
        Future.failed(ex)
    }
  }
}
