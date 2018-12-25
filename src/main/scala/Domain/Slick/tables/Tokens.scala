package Domain.Slick.tables

import slick.jdbc.H2Profile.api._

package object Tokens {

  class Tokens(tag: Tag)
    extends Table[(Long, String, String, String, Long)](tag, "TOKENS") {

    def userId: Rep[Long] = column[Long]("USER_ID", O.PrimaryKey)
    def selector: Rep[String] = column[String]("SELECTOR")
    def token: Rep[String] = column[String]("TOKEN")
    def userRole: Rep[String] = column[String]("USER_ROLE")
    def expires: Rep[Long] = column[Long]("EXPIRES")

    def idx = index("idx_tokens", selector)
    def * = (userId, selector, token, userRole, expires)
  }

  lazy val tokens = TableQuery[Tokens]
}
