package Api.repo

import Api.session.UserSession
import Domain.Slick.DatabaseManager
import Domain.Slick.tables.Tokens.tokens
import com.softwaremill.session.{RefreshTokenData, RefreshTokenLookupResult, RefreshTokenStorage}
import exceptions.TokenNotFoundException
import com.typesafe.scalalogging.StrictLogging

import slick.jdbc.H2Profile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RefreshTokenStorageImpl(private val database: DatabaseManager)
  extends RefreshTokenStorage[UserSession]
  with StrictLogging {

  override def lookup(selector: String): Future[Option[RefreshTokenLookupResult[UserSession]]] = {
    val foundToken = for {
      token <- tokens.filter(_.selector === selector)
    } yield (token.token, token.expires, token.userId, token.userRole)

    database.exec(foundToken.result)
      .collect {
        case foundTokens if foundTokens.nonEmpty =>
          log(s"Looking up token for selector: $selector found")
          Some(RefreshTokenLookupResult(foundTokens.head._1, foundTokens.head._2,
            () => UserSession(foundTokens.head._3, foundTokens.head._4)))

        case _ =>
          log(s"Looking up token for selector: $selector not found")
          throw TokenNotFoundException("token not found", null)
      }

  }

  override def store(data: RefreshTokenData[UserSession]): Future[Unit] = {

    log(s"Storing token for selector: ${data.selector}, user: ${data.forSession.userId}, " +
        s"expires: ${data.expires}, now: ${System.currentTimeMillis()}")

    val updatedTokens = for {
      tokenData <- tokens.filter(_.userId === data.forSession.userId)
    } yield (tokenData.selector, tokenData.token, tokenData.expires)

    database
      .exec(updatedTokens.update((data.selector, data.tokenHash, data.expires)))
      .map(_ => Unit)
  }

  override def remove(selector: String): Future[Unit] = {
    log(s"Removing token for selector: $selector")

    val tokensForRemove = tokens.filter(_.selector === selector)
    database.exec(tokensForRemove.delete)
      .map(_ => Unit)
  }

  override def schedule[S](after: Duration)(op: => Future[S]): Unit = {
    log("Running scheduled operation immediately")
    op
    Future.successful(())
  }

  def log(msg: String): Unit = logger.info(msg)
}
