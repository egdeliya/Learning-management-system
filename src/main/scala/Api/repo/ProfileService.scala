package Api.repo

import scala.concurrent.Future

trait ProfileService {
  def myProfile(uid: Long): Future[Map[String, String]]

  def updateProfile(data: Map[String, String]): Future[Unit]

  def profile(uid: Long): Future[Map[String, String]]
}
