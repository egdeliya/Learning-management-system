import scala.concurrent.Future

trait AdminService {

  def createGroup(groupName: String,
                  facultyName: String,
                  courseNum: Int): Future[Unit]

  def createCourse(courseName: String,
                   courseDescription: String): Future[Unit]

  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit]

  def addTeacherToCourse(teacherId: Int,
                         courseName: String): Future[Unit]

  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Int,
                 grade: String,
                 form: String,
                 basis: String): Future[(String, Int)]

  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[(String, Int)]

}
