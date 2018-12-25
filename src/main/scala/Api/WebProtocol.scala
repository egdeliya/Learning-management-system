package Api
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class LoginRequest(email: String, password: String)
case class RegisterRequest(email: String, password: String, token: String)

trait WebProtocol extends SprayJsonSupport{
  import DefaultJsonProtocol._

  object WebStatus {
    val Ok = "OK"
    val Error = "Error"
  }

  implicit val loginRequestFormat = jsonFormat2(LoginRequest)
  implicit val registerRequestFormat = jsonFormat3(RegisterRequest)
}
