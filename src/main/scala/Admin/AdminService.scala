package Admin

import scala.concurrent.Future

trait AdminService {

  /**
    * Создает группу
    * @return Future.failed(ex: Throwable) в случае неудачи, Future.successful() иначе
    */
  def createGroup(groupName: String,
                  facultyName: String,
                  courseNum: Long): Future[Unit]

  /**
    * Создает курс
    * @return Future.failed(ex: Throwable) в случае неудачи, Future.successful() иначе
    */
  def createCourse(courseName: String,
                   courseDescription: String): Future[Unit]

  /**
    * Добавляет группу к курсу
    * @return Future.failed(ex: Throwable) в случае неудачи, Future.successful() иначе
    */
  def addGroupToCourse(groupName: String,
                       courseName: String): Future[Unit]

  /**
    * Добавляет преподавателя к курсу
    * @return Future.failed(ex: Throwable) в случае неудачи, Future.successful() иначе
    */
  def addTeacherToCourse(teacherId: Long,
                         courseName: String): Future[Unit]

  /**
    * Добавляет пользователя - студента
    * @return Future от userId, userToken
    */
  def addStudent(name: String,
                 surname: String,
                 patronymic: String,
                 group: String,
                 entryYear: Long,
                 grade: String,
                 form: String,
                 basis: String): Future[(String, Long)]

  /**
    * Добавляет пользователя - преподавателя
    * @return Future от userId, userToken
    */
  def addTeacher(name: String,
                 surname: String,
                 patronymic: String): Future[(String, Long)]

}
