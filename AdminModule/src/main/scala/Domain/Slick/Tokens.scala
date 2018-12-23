package Domain.Slick

import java.sql.Date
import java.util.UUID

import slick.jdbc.H2Profile.api._


package object Tokens {

  class Tokens(tag: Tag)
    extends Table[(Int, UUID, String, Date)](tag, "TOKENS") {

    def userId: Rep[Int] = column[Int]("USER_ID", O.PrimaryKey)
    def token: Rep[UUID] = column[UUID]("TOKEN")
    def userRole: Rep[String] = column[String]("USER_ROLE")
    def expired: Rep[Date] = column[Date]("EXPIRED_TIME")

    def idx = index("idx_tokens", token)
    def * = (userId, token, userRole, expired)
  }

  lazy val tokens = TableQuery[Tokens]
}

