package Domain.Slick.tables

import slick.jdbc.H2Profile.api._

package object Auths {

  class Auths(tag: Tag)
    extends Table[(Long, String, String)](tag, "AUTHS") {

    def userId: Rep[Long] = column[Long]("USER_ID", O.PrimaryKey)
    def email: Rep[String] = column[String]("EMAIL")
    def pwdHash: Rep[String] = column[String]("PASSWORD")

    def idx = index("idx_auths", email, unique = true)
    def * = (userId, email, pwdHash)
  }

  lazy val auths = TableQuery[Auths]
}
