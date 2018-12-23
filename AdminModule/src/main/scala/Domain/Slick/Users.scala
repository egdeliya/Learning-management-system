package Domain.Slick

import Domain.Models.User
import Students._
import Teachers._

import slick.jdbc.H2Profile.api._

package object Users {

  class Users(tag: Tag)
    extends Table[User](tag, "USERS") {

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("NAME")
    def surname: Rep[String] = column[String]("SURNAME")
    def patronymic: Rep[String] = column[String]("PATRONYMIC")

//    def student = foreignKey("fk_student", id, students)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
//    def teacher = foreignKey("fk_teacher", id, teachers)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    def idx = index("idx_users", id, unique = true)
    def * = (id.?, name, surname, patronymic) <> (User.tupled, User.unapply)
  }

  lazy val users = TableQuery[Users]
}

