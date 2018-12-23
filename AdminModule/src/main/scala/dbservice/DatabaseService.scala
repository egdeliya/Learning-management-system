package dbservice

import Domain.Models.{Course, Group, Student, Teacher}

import scala.concurrent.Future

trait DatabaseService {

  def createGroup(group: Group): Future[Unit]

  def createCourse(course: Course): Future[Unit]

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit]

  def addTeacherToCourse(teacherId: Int,
                         courseName: String): Future[Unit]

  def addStudent(student: Student): Future[(String, Int)]

  def addTeacher(teacher: Teacher): Future[(String, Int)]

}
