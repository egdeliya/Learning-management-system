package Api

import Admin.repo.AdminRepoImpl
import Admin.AdminServiceImpl
import Api.repo.{AuthServiceImpl, RefreshTokenStorageImpl}
import Api.session.UserSessionManager
import Domain.Models.User
import Domain.Slick.DatabaseManager
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
//import akka.http.scaladsl.server._
//import akka.http.scaladsl.server.BasicDirectives._
import akka.http.scaladsl.server.Directives._

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration.Duration
import scala.concurrent.Await

trait testUsersData {
  val user: User = User(None, "Test", "Hello", "World")
  val email = "email@email.com"
  val password = "Ue4kkk23Q"
}

trait initApp extends testUsersData {
  val dbManager = new DatabaseManager("lmsdbtest")
  dbManager.dropTables()
  dbManager.createTablesIfNotInExists()

  private val dbService = new AdminRepoImpl(dbManager)
  val adminService = new AdminServiceImpl(dbService)

  val conf = ConfigFactory.load()
  val authService = new AuthServiceImpl(dbManager)
  val refreshTokenStorage = new RefreshTokenStorageImpl(dbManager)
  val userSessionManager = new UserSessionManager(conf)

  val webServer = new WebServer(authService, conf, userSessionManager, refreshTokenStorage)
  lazy val route = webServer.getRoute()

  val (token, userId) = Await.result(adminService
    .addTeacher(user.name, user.surname, user.patronymic),
    Duration.Inf)
}

class ApiRegisterTest extends FlatSpec
  with Matchers
  with WebProtocol
  with ScalatestRouteTest {

  "WebServer" should "register user successfully" in new initApp {
    val registerRequest = RegisterRequest(email, password, token)
    Post("/register", registerRequest) ~> route ~> check {
      responseAs[String] shouldEqual WebStatus.Ok
      status shouldBe StatusCodes.Created
    }

    dbManager.close
  }

  "WebServer" should "fail registering user with invalid email" in new initApp {
    val invalidEmail = "blabla.b.o.o.m"
    val registerRequest = RegisterRequest(invalidEmail, password, token)
    Post("/register", registerRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Incorrect email"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

  "WebServer" should "fail registering user with password without upper case symbols" in new initApp {
    val weakPassword = "blabla.b.o.o.m"
    val registerRequest = RegisterRequest(email, weakPassword, token)
    Post("/register", registerRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Password should contain at least one upper case symbol"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

  "WebServer" should "fail registering user with password without lower case symbols" in new initApp {
    val weakPassword = "ASDSDA23"
    val registerRequest = RegisterRequest(email, weakPassword, token)
    Post("/register", registerRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Password should contain at least one lower case symbol"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

  "WebServer" should "fail registering user with password without numbers" in new initApp {
    val weakPassword = "ASDSDAasas"
    val registerRequest = RegisterRequest(email, weakPassword, token)
    Post("/register", registerRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Password should contain at least one number"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

  "WebServer" should "fail registering user with short password" in new initApp {
    val weakPassword = "ASa7a"
    val registerRequest = RegisterRequest(email, weakPassword, token)
    Post("/register", registerRequest) ~> route ~> check {
      assert(responseAs[String].contains("Password length should be at least"))
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

}
