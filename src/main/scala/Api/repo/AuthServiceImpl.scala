package Api.repo

import Domain.Slick.tables.Auths._
import Domain.Slick.tables.Tokens._
import Domain.Slick.DatabaseManager
import com.typesafe.scalalogging.StrictLogging
import exceptions.UserNotFoundException
import org.mindrot.jbcrypt.BCrypt
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthServiceImpl(private val database: DatabaseManager) extends AuthService
  with StrictLogging {

  def login(email: String, password: String): Future[(Long, String)] = {
    val foundCredentials = for {
      auth <- auths.filter(_.email === email)
    } yield (auth.pwdHash, auth.userId)

    database.exec(foundCredentials.result)
      .flatMap {
       case userData
         if userData.nonEmpty && BCrypt.checkpw(password, userData.head._1) =>
            val userSessionData = for {
              tokensData <- tokens.filter(_.userId === userData.head._2)
            } yield (tokensData.userId, tokensData.userRole)

            database.exec(userSessionData.result)
              .map(data => data.head)

       case _ =>
         logger.info(s"email: $email not found")
         throw UserNotFoundException("Email or password incorrect", null)
      }
  }

  def register(token: String, email: Email, password: Password): Future[Unit] = {
   val foundCredentials = for {
     credentials <- tokens.filter(_.token === token)
   } yield credentials.userId

    database.exec(foundCredentials.result)
      .flatMap {
        case userData if userData.nonEmpty =>
          logger.info("User found")
          database.exec(DBIO.seq(auths += (userData.head,
                                            email.getValue,
                                            BCrypt.hashpw(password.getValue, BCrypt.gensalt())))
          )

        case _ =>
          logger.info("User not found in register")
          throw UserNotFoundException("Wrong token", null)
      }
  }

}
