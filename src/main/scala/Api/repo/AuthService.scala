package Api.repo

import exceptions.{IncorrectEmailException, WeakPasswordException}

import scala.concurrent.Future
import scala.util.matching.Regex

object Email {
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def validate(email: String): Boolean = email match{
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }

  def apply(value: String): Email = {
    if (validate(value)) new Email(value)
    else throw IncorrectEmailException("Incorrect email", null)
  }
}
class Email private (private val value: String) {
  val getValue = value
}

object Password {
  private val upperCaseCheck = "[A-Z]".r
  private val lowerCaseCheck = "[a-z]".r
  private val numCheck = "[0-9]".r
  private val safeEnoughLen = 6

  private def lenCheck(value: String) = value.length > safeEnoughLen
  private def regexMatch(regex: Regex, value: String) = regex.findFirstMatchIn(value).isDefined

  def apply(value: String): Password = {
    if (!regexMatch(upperCaseCheck, value)) throw WeakPasswordException("Password should contain at least one upper case symbol", null)
    else if (!regexMatch(lowerCaseCheck, value)) throw WeakPasswordException("Password should contain at least one lower case symbol", null)
    else if (!regexMatch(numCheck, value)) throw WeakPasswordException("Password should contain at least one number", null)
    else if (!lenCheck(value)) throw WeakPasswordException(s"Password length should be at least $safeEnoughLen", null)
    else new Password(value)
  }
}
class Password private (private val value: String) {
  val getValue = value
}

trait AuthService {

  /**
    * Вход пользователя по e-mail и паролю
    * @return Future от userId, userRole - ("teacher", "student")
    */
  def login(email: String, password: String): Future[(Long, String)]

  /**
    * Регистрация пользователя по токену, e-mail и паролю
    * @return Future.successful() - в случае удачи, Future.failed() иначе
    */
  def register(token: String, email: Email, password: Password): Future[Unit]
}
