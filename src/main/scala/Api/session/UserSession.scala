package Api.session

import com.softwaremill.session.{MultiValueSessionSerializer, SessionSerializer}

import scala.util.Try

case class UserSession(userId: Long, role: String)

object UserSession {
  implicit def serializer: SessionSerializer[UserSession, String] =
    new MultiValueSessionSerializer[UserSession](
      userSession => Map("userId" -> userSession.userId.toString, "role" -> userSession.role),
      userSessionMap => Try { UserSession(userSessionMap("userId").toLong, userSessionMap("role")) }
    )
}
