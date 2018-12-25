package Api

import Api.repo.{AuthServiceImpl, RefreshTokenStorageImpl}
import Api.session.UserSessionManager
import Domain.Slick.DatabaseManager
import com.typesafe.config.ConfigFactory

object ApplicationApp extends App {

  val conf = ConfigFactory.load()
  val dbManager = new DatabaseManager("lmsdb")
  dbManager.createTablesIfNotInExists()
  val authService = new AuthServiceImpl(dbManager)
  val refreshTokenStorage = new RefreshTokenStorageImpl(dbManager)
  val userSessionManager = new UserSessionManager(conf)

  val webServer = new WebServer(authService, conf, userSessionManager, refreshTokenStorage)
  webServer.run()
}
