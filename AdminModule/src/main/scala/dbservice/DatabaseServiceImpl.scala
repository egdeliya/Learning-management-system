package dbservice

import Domain.Models.{Course, Group, Student, Teacher}
import Domain.Slick.Courses._
import Domain.Slick.Groups._
import Domain.Slick.GroupToCourse._
import Domain.Slick.TeacherToCourse._
import Domain.Slick.Users._
import Domain.Slick.Students._
import Domain.Slick.Teachers._
import Domain.Slick.Tokens._

import org.joda.time.DateTime
import java.sql.Date
import java.util.UUID

import dbservice.exceptions._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseServiceImpl(private val database: DatabaseManager) extends DatabaseService {

  def createGroup(group: Group): Future[Unit] = {
    database.exec(DBIO.seq(groups += group))
  }

  def createCourse(course: Course): Future[Unit] = {
    database.exec(DBIO.seq(courses += course))
  }

  private def checkCourseExistence(courseName: String): Future[Boolean] = {
    val foundCourses = for {
      _ <- courses.filter(_.name === courseName)
    } yield courseName

    database.exec(foundCourses.result)
      .map(courses => courses.nonEmpty)
  }

  private def checkGroupExistence(groupName: String): Future[Boolean] = {
    val foundGroup = for {
      _ <- groups.filter(_.name === groupName)
    } yield groupName

    database.exec(foundGroup.result)
      .map(groups => {
        groups.nonEmpty
      })
  }

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit] = {
    checkCourseExistence(courseName)
      .zip(checkGroupExistence(groupName))
      .flatMap {
        case (courseExists, groupExists) if courseExists && !groupExists =>
          throw GroupNotFoundException(s"group $groupName does not exist", null)

        case (courseExists, groupExists) if !courseExists && groupExists =>
          throw CourseNotFoundException(s"course $courseName does not exist", null)

        case (courseExists, groupExists) if !courseExists && !groupExists =>
          throw new IllegalArgumentException(s"no course $courseName and group $groupName")

        case _ =>
          database.exec(DBIO.seq(groupToCourse += (groupName, courseName)))
      }
  }

  private def checkTeacherExistence(teacherId: Int): Future[Boolean] = {
    val foundTeacher = for {
      _ <- teachers.filter(_.id === teacherId)
    } yield teacherId

    database.exec(foundTeacher.result)
      .map(teachers => teachers.nonEmpty)
  }

  def addTeacherToCourse(teacherId: Int,
                         courseName: String): Future[Unit] = {
    checkCourseExistence(courseName)
      .zip(checkTeacherExistence(teacherId))
      .flatMap {
        case (courseExists, teacherExists) if courseExists && !teacherExists =>
          throw GroupNotFoundException(s"teacher with id $teacherId does not exist", null)

        case (courseExists, teacherExists) if !courseExists && teacherExists =>
          throw CourseNotFoundException(s"course $courseName does not exist", null)

        case (courseExists, teacherExists) if !courseExists && !teacherExists =>
          throw new IllegalArgumentException(s"no course $courseName and teacher with id $teacherId")

        case _ =>
          database.exec(DBIO.seq(teacherToCourse += (teacherId, courseName)))
      }
  }

  private def generateUserToken(uid: Int, role: String): Future[String] = {
    val token = UUID.randomUUID()

    // TODO add token duration to config
    val expired = new Date(DateTime.now.plusMinutes(10).getMillis)

    database.exec(DBIO.seq(tokens += (uid, token, role, expired)))
      .map(_ => token.toString)
  }

  def addStudent(student: Student): Future[(String, Int)] = {
    checkGroupExistence(student.group)
      .flatMap {
        case groupExist if groupExist =>
          database.exec(users returning users.map(_.id) ++= Seq(student.user))
            .flatMap {
              ids => {
                database.exec(DBIO.seq(students += (ids.head,
                  student.group,
                  student.entryYear.value,
                  student.degree.value,
                  student.educationForm.value,
                  student.basis.value
                )))
                  .flatMap(_ => generateUserToken(ids.head, "student"))
                  .map(token => (token, ids.head))
              }
            }

        case groupExist if !groupExist =>
          throw GroupNotFoundException(s"group ${student.group} does not exist", null)
      }
  }

  def addTeacher(teacher: Teacher): Future[(String, Int)] = {
    val teacherId = database.exec(users returning users.map(_.id) ++= Seq(teacher.user))
    teacherId
      .flatMap {
        ids =>
          database.exec(DBIO.seq(teachers += ids.head))
            .flatMap(_ => generateUserToken(ids.head, "teacher"))
            .map(token => (token, ids.head))
      }
  }
}
