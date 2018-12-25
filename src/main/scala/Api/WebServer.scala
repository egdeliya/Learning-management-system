package Api

import Api.repo.{AuthService, Email, Password}
import Api.session.{UserSession, UserSessionManager}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.stream.ActorMaterializer
import com.softwaremill.session.CsrfDirectives._
import com.softwaremill.session.CsrfOptions._
import com.softwaremill.session.RefreshTokenStorage
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import exceptions.{IncorrectEmailException, UserNotFoundException, WeakPasswordException}

import scala.io.StdIn

class WebServer(private val authService: AuthService,
                private val conf: Config,
                private val userSessionManager: UserSessionManager,
                private val refreshTokenStorage: RefreshTokenStorage[UserSession])
  extends Directives
  with WebProtocol
  with StrictLogging {

  implicit val system = ActorSystem("lms")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  private val endpoint = conf.getString("webserver.endpoint")
  private val port = conf.getInt("webserver.port")

  val refr = refreshable(
    userSessionManager.sessionManager,
    refreshTokenStorage,
    ec)

  def setUserSession(session: UserSession) =
    setSession(refr, usingCookies, session)
  val myRequiredSession = requiredSession(refr, usingCookies)
  val myInvalidateSession = invalidateSession(refr, usingCookies)

  def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case th @ (WeakPasswordException(_, _) | UserNotFoundException(_, _) | IncorrectEmailException(_, _)) =>
      complete(HttpResponse(400, entity = th.getMessage))
    case th: InternalError =>
      complete (HttpResponse(500, entity = th.getMessage))
    case th: Throwable =>
      complete (HttpResponse(500, entity = th.getMessage))
  }

  val route =
    handleExceptions(myExceptionHandler) {
      path("login") {
        post {
          entity(as[LoginRequest]) { loginRequest =>
            logger.info(s"Logging in ${loginRequest.email}")

            val result = authService.login(loginRequest.email, loginRequest.password)
              .map { userData =>
                setUserSession(UserSession(userData._1, userData._2)) {
                  setNewCsrfToken(checkHeader(userSessionManager.sessionManager)) { ctx =>
                    ctx.complete(HttpResponse(202, entity = WebStatus.Ok))
                  }
                }
              }

            onSuccess(result) { res =>
              res
            }
          }
        }
      } ~
        path("register") {
          post {
            entity(as[RegisterRequest]) { registerRequest =>
              logger.info(s"Registering in ${registerRequest.email}")

              val result = authService
                .register(registerRequest.token, Email(registerRequest.email), Password(registerRequest.password))

              onSuccess(result) { ctx =>
                ctx.complete(HttpResponse(201, entity = WebStatus.Ok))
              }

            }
          }
      }
    }

  def getRoute() = {
    route
  }

  def run() = {
    val bindingFuture = Http().bindAndHandle(route, endpoint, port)
    println(s"Server online at http://$endpoint:$port/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
