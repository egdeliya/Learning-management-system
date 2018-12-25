package Api

import Api.repo.{Email, Password}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.server._
import Directives._

import scala.concurrent.duration.Duration
import scala.concurrent.Await

trait initAppWithRegisteredUser extends initApp {
  Await.result(authService.register(token, Email(email), Password(password)), Duration.Inf)
}

class ApiLoginTest extends FlatSpec
  with Matchers
  with WebProtocol
  with ScalatestRouteTest {

  "WebServer" should "login user successfully" in new initAppWithRegisteredUser {
    val loginRequest = LoginRequest(email, password)
    Post("/login", loginRequest) ~> route ~> check {
      responseAs[String] shouldEqual WebStatus.Ok
      status shouldBe StatusCodes.Accepted
    }

    dbManager.close
  }

  "WebServer" should "fail on login user with invalid password" in new initAppWithRegisteredUser {
    val invalidPassword = "Ue4kkk23"
    val loginRequest = LoginRequest(email, invalidPassword)
    Post("/login", loginRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Email or password incorrect"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

  "WebServer" should "fail on login user with invalid email" in new initAppWithRegisteredUser {
    val invalidEmail = "mail@email.com"
    val loginRequest = LoginRequest(invalidEmail, password)
    Post("/login", loginRequest) ~> route ~> check {
      responseAs[String] shouldEqual "Email or password incorrect"
      status shouldBe StatusCodes.BadRequest
    }

    dbManager.close
  }

}
