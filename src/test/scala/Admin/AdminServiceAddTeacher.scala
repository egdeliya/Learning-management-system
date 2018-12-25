package Admin

import Admin.repo.AdminRepoImpl
import Domain.Models.User
import Domain.Slick.DatabaseManager
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

trait testUsersData {
  private val userNames = Seq("Test")
  private val userSurnames = Seq("Hello")
  private val userPatronymics = Seq("World")

  val users: Seq[User] = for {
    userName <- userNames
    surname <- userSurnames
    patronymic <- userPatronymics
  } yield User(None, userName, surname, patronymic)
}

class AdminServiceAddTeacherTest extends FlatSpec
  with Matchers
  with testUsersData {

  trait init {
    val dbManager = new DatabaseManager("lmsdbtest")
    dbManager.dropTables()
    dbManager.createTablesIfNotInExists()

    private val dbService = new AdminRepoImpl(dbManager)
    val adminService = new AdminServiceImpl(dbService)

    def addTeachers(): Future[Seq[(String, Long)]] = {
      Future.sequence(
        users.map { user =>
          adminService.addTeacher(user.name, user.surname, user.patronymic)
        })
    }
  }

  "Admin.AdminService" should "add teacher successfully" in new init {
    val result = addTeachers()
    .transform(Success(_))
    .collect { case Success(_) => "success" }

    val res = Await.result(result, Duration.Inf)

    res shouldBe "success"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding user with empty name" in new init {
    val result = Future.sequence(users.map { user =>
      adminService.addTeacher("", user.surname, user.patronymic)
    }.map(_.transform(Success(_)))
    ).map(_.collect { case Failure(ex) => ex })

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: name must be non empty!"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding user with empty surname" in new init {
    val result = Future.sequence(users.map { user =>
      adminService.addTeacher(user.name, "", user.patronymic)
    }.map(_.transform(Success(_)))
    ).map(_.collect { case Failure(ex) => ex })

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: surname must be non empty!"
    dbManager.close
  }

  "Admin.AdminService" should "fail adding user with empty patronymic" in new init {
    val result = Future.sequence(users.map { user =>
      adminService.addTeacher(user.name, user.surname, "")
    }.map(_.transform(Success(_)))
    ).map(_.collect { case Failure(ex) => ex })

    val exception = Await.result(result, Duration.Inf)

    exception should have size 1
    exception.head.getMessage shouldBe "requirement failed: patronymic must be non empty!"
    dbManager.close
  }
}
