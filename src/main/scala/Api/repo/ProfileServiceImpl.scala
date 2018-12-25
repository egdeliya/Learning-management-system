package Api.repo

import Domain.Slick.DatabaseManager
import com.typesafe.scalalogging.StrictLogging
import exceptions.InvalidLinkException

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object VkLink {
  private val vkLinkCheck = """^(https://vk.com/){1}(.+)""".r
  private def validate(value: String) = {
    Try {
      val vkLinkCheck(_, _) = value
    }.isSuccess
  }

  def apply(value: String): VkLink = {
    if (validate(value)) new VkLink(value)
    else throw InvalidLinkException(s"Invalid link $value", null)
  }
}
class VkLink private(private val value: String) {
  def getValue = value
}

class ProfileServiceImpl(private val dbManager: DatabaseManager) extends ProfileService
  with StrictLogging {

  def myProfile(uid: Long): Future[Map[String, String]] = {
    Future.successful(Map.empty)
  }

  def updateProfile(data: Map[String, String]): Future[Unit] = {
    Future.successful()
  }

  def profile(uid: Long): Future[Map[String, String]] = {
    Future.successful(Map.empty)
  }
}

