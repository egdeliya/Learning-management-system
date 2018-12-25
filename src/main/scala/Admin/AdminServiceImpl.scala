package Admin

import Admin.repo.AdminRepo
import Domain.Models.{User, _}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class AdminServiceImpl(private val adminRepository: AdminRepo)
  extends AdminService {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def createGroup(groupName: String,
                  facultyName: String,
                  courseNum: Long): Future[Unit] = {
    Try {
      Group(groupName, facultyName, courseNum)
    }.map { group =>
      adminRepository.createGroup(group)
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
      adminRepository.createCourse(course)
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"createCourse: failed creating course ${courseName} with description ${courseDescription}")
        Future.failed(ex)
    }
  }

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit] = {
    adminRepository.addGroupToCourse(groupName, courseName)
  }

  def addTeacherToCourse(teacherId: Long,
                         courseName: String): Future[Unit] = {
    adminRepository.addTeacherToCourse(teacherId, courseName)
  }

  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Long,
                 grade: String,
                 form: String,
                 basis: String): Future[(String, Long)] = {
    Try {
      val user = User(None, name, surname, patronymic)
      Student(user,
              group,
              EntryYear(entryYear),
              Degree(grade),
              EducationForm(form),
              Basis(basis))

    }.map { student =>
      adminRepository.addStudent(student)
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"addStudent: failed adding Student")
        Future.failed(ex)
    }
  }

  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[(String, Long)] = {
    Try {
      User(None, name, surname, patronymic)
    }.map { user =>
      adminRepository.addTeacher(Teacher(user))
    } match {
      case Success(value) => value
      case Failure(ex) =>
        log.warn(s"addTeacher: failed adding teacher")
        Future.failed(ex)
    }
  }
}
