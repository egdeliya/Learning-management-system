package Admin.repo

import Domain.Models.{Course, Group, Student, Teacher}

import scala.concurrent.Future

trait AdminRepo {

  def createGroup(group: Group): Future[Unit]

  def createCourse(course: Course): Future[Unit]

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit]

  def addTeacherToCourse(teacherId: Long,
                         courseName: String): Future[Unit]

  def addStudent(student: Student): Future[(String, Long)]

  def addTeacher(teacher: Teacher): Future[(String, Long)]

}
