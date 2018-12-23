import Domain.Models._
import org.scalatest.{FlatSpec, Matchers}
import dbservice.{DatabaseManager, DatabaseServiceImpl}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait testGroupsData {
  private val groupNames = Seq("testGroup")
  private val courseNums = Seq(1)
  private val faculties = Seq("testFaculty")

  val groups: Seq[Group] = for {
    (groupName, courseNum) <- groupNames zip courseNums
    faculty <- faculties
  } yield Group(groupName, faculty, courseNum)
}

class AdminServiceCreateGroupTest extends FlatSpec
  with Matchers
  with testGroupsData {

  trait init {
    val dbManager = new DatabaseManager("lmsdbtest")
    dbManager.dropTables()
    dbManager.createTablesIfNotInExists()

    private val dbService = new DatabaseServiceImpl(dbManager)
    val adminService = new AdminServiceImpl(dbService)

    def createGroups(): Future[Seq[Unit]] = {
      Future.sequence(
        groups.map { group =>
          adminService.createGroup(group.groupName, group.facultyName, group.courseNum)
        })
    }
  }

  "AdminService" should "create group successfully" in new init {
    val result = Future.sequence(groups.map { group =>
        adminService.createGroup(group.groupName, group.facultyName, group.courseNum)
      }.map(_.transform(Success(_)))
    ).map(_.collect { case Success(_) => "success" })

    val res = Await.result(result, Duration.Inf)

    res should have size 1
    res.foreach(res => res shouldBe "success")
    dbManager.close
  }

  "AdminService" should "fail creating group with empty name" in new init {
    val result = Future.sequence(groups.map { group =>
      adminService.createGroup("", group.facultyName, group.courseNum)
    }.map(_.transform(Success(_)))
    ).map(_.collect{ case Failure(x) => x })

    val exceptions = Await.result(result, Duration.Inf)

    exceptions should have size 1
    exceptions.head.getMessage shouldBe "requirement failed: group name must be non empty!"
    dbManager.close
  }

  "AdminService" should "fail creating group with empty faculty" in new init {
    val result = Future.sequence(groups.map { group =>
      adminService.createGroup(group.groupName, "", group.courseNum)
    }.map(_.transform(Success(_)))
    ).map(_.collect{ case Failure(x) => x })

    val exceptions = Await.result(result, Duration.Inf)

    exceptions should have size 1
    exceptions.head.getMessage shouldBe "requirement failed: faculty name must be non empty!"
    dbManager.close
  }

  "AdminService" should "fail creating group with negative course number" in new init {
    val result = Future.sequence(groups.map { group =>
      adminService.createGroup(group.groupName, group.groupName, -1)
    }.map(_.transform(Success(_)))
    ).map(_.collect{ case Failure(x) => x })

    val exceptions = Await.result(result, Duration.Inf)

    exceptions should have size 1
    exceptions.head.getMessage shouldBe "requirement failed: course number must be positive and less than 7!"
    dbManager.close
  }
}
