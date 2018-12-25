package Admin

import scala.concurrent.Future

trait AdminService {

  def createGroup(groupName: String,
                  facultyName: String,
                  courseNum: Long): Future[Unit]

  def createCourse(courseName: String,
                   courseDescription: String): Future[Unit]

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit]

  def addTeacherToCourse(teacherId: Long,
                         courseName: String): Future[Unit]

  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Long,
                 grade: String,
                 form: String,
                 basis: String): Future[(String, Long)]

  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[(String, Long)]

}
