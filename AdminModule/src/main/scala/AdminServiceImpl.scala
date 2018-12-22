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
      dbService.createGroup(Group(groupName, facultyName, courseNum))
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
      dbService.createCourse(Course(courseName, courseDescription))
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
      .recover {
        case ex: Throwable =>
          log.debug(s"addGroupToCourse: failed adding group ${groupName} to course ${courseName}")
          Future.failed(ex)
      }
  }

  def addTeacherToCourse(teacherId: Int,
                         courseName: String): Future[Unit] = {
    dbService.addTeacherToCourse(teacherId, courseName)
      .recover {
        case ex: Throwable =>
          log.debug(s"addTeacherToCourse: failed adding teacher ${teacherId} to course ${courseName}")
          Future.failed(ex)
      }
  }

  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Int,
                 grade: String,
                 form: String,
                 basis: String): Future[Unit] = {
    Try {
      val user = User(None, name, surname, patronymic)
      val student = Student(user,
                    group,
                    EntryYear(entryYear),
                    Degree(grade),
                    EducationForm(form),
                    Basis(basis))

      dbService.addStudent(student)
        .recover {
          case ex: Throwable =>
            log.warn(s"addStudent: failed with message ${ex.getMessage}")
            Future.failed(ex)
        }
    } match {
      case Success(_) => Future.successful()
      case Failure(ex) =>
        log.warn(s"addStudent: failed adding Student")
        Future.failed(ex)
    }
  }

  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[Unit] = {
    Try {
      val user = User(None, name, surname, patronymic)
      dbService.addTeacher(Teacher(user))
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"addTeacher: failed adding teacher")
        Future.failed(ex)
    }
  }
}
