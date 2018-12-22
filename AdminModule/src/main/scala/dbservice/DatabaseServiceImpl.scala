package dbservice

import Domain.Models.{Course, Group, Student, Teacher}
import Domain.Slick.Courses._
import Domain.Slick.Groups._
import Domain.Slick.GroupToCourse._
import Domain.Slick.TeacherToCourse._
import Domain.Slick.Users._
import Domain.Slick.Students._
import Domain.Slick.Teachers._
import com.typesafe.scalalogging.Logger

import dbservice.exceptions._

import org.slf4j.LoggerFactory
import slick.jdbc.SQLiteProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DatabaseServiceImpl extends DatabaseService {

  import DatabaseManager._

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def createGroup(group: Group): Future[Unit] = {
    exec(DBIO.seq(groups += group))
  }

  def createCourse(course: Course): Future[Unit] = {
    exec(DBIO.seq(courses += course))
  }

  private def checkCourseExistence(courseName: String): Future[Boolean] = {
    val foundCourses = for {
      _ <- courses.filter(_.name === courseName)
    } yield courseName

    exec(foundCourses.result)
      .map(courses => courses.nonEmpty)
  }

  private def checkGroupExistence(groupName: String): Future[Boolean] = {
    val foundGroup = for {
      _ <- groups.filter(_.name === groupName)
    } yield groupName

    exec(foundGroup.result)
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
          exec(DBIO.seq(groupToCourse += (groupName, courseName)))
      }
  }

  private def checkTeacherExistence(teacherId: Int): Future[Boolean] = {
    val foundTeacher = for {
      _ <- teachers.filter(_.id === teacherId)
    } yield teacherId

    exec(foundTeacher.result)
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
          exec(DBIO.seq(teacherToCourse += (teacherId, courseName)))
      }
  }

  def addStudent(student: Student): Future[Int] = {
    checkGroupExistence(student.group)
      .flatMap {
        case groupExist if groupExist =>
          exec(users returning users.map(_.id) ++= Seq(student.user))
            .flatMap {
              ids => {
                exec(DBIO.seq(students += (ids.head,
                  student.group,
                  student.entryYear.value,
                  student.grade.value,
                  student.educationForm.value,
                  student.basis.value
                )))
                  .map(_ => ids.head)
              }
            }

        case groupExist if !groupExist =>
          log.warn(s"group ${student.group} does not exist")
          throw GroupNotFoundException(s"group ${student.group} does not exist", null)
      }
  }

  def addTeacher(teacher: Teacher): Future[Int] = {
    val teacherId = exec(users returning users.map(_.id) ++= Seq(teacher.user))
    teacherId
      .flatMap {
        ids =>
          exec(DBIO.seq(teachers += ids.head))
            .map(_ => ids.head)
      }
  }
}
