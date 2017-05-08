package transfers

import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._

class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest {
  "Service" should {
    "provide addUser operation" which {
      "creates new user" in {
        Put("/user/1") ~> Route.route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }
      "denies creation" when {
        "user already exists" in {
          Put("/user/1") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "User already exists."
          }
        }
      }
    }

    "provide getFunds operation" which {
      "returns users funds" in {
        Get("/user/1") ~> Route.route ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[String] shouldEqual "0"
        }
      }
      "denies" when {
        "there is no such user" in {
          Get("/user/2") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "No such user."
          }
        }
      }
    }

    "provide deposit operation" which {
      "increases users funds" in {
        Post("/user/1/deposit?amount=1000") ~> Route.route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }
      "denies" when {
        "there is no such user" in {
          Post("/user/2/deposit?amount=1000") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "No such user."
          }
        }
      }
    }

    "provide withdraw operation" which {
      "decreases users funds" in {
        Post("/user/1/withdraw?amount=100") ~> Route.route ~> check {
          status shouldEqual StatusCodes.Created
        }
      }
      "denies" when {
        "there is no such user" in {
          Post("/user/2/withdraw?amount=100") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "No such user."
          }
        }
      }
      "denies" when {
        "there is not enough money in users funds" in {
          Post("/user/1/withdraw?amount=1000") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "Insufficient funds."
          }
        }
      }
    }

    "provide transfer operation" which {
      Put("/user/11") ~> Route.route
      Post("/user/11/deposit?amount=1000") ~> Route.route
      Put("/user/12") ~> Route.route
      "moves funds from one user to another" in {
        Post("/user/11/transfer/12?amount=100") ~> Route.route ~> check {
          status shouldEqual StatusCodes.Created
        }
        Get("/user/11") ~> Route.route ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[String] shouldEqual "900"
        }
        Get("/user/12") ~> Route.route ~> check {
          status shouldEqual StatusCodes.OK
          responseAs[String] shouldEqual "100"
        }
      }
      "denies" when {
        "there is no such sorce user" in {
          Post("/user/13/transfer/12?amount=100") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "No such user."
          }
        }
      }
      "denies" when {
        "there is no such destination user" in {
          Post("/user/11/transfer/13?amount=100") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "No such user."
          }
        }
      }
      "denies" when {
        "there is not enough money in source users funds" in {
          Post("/user/11/transfer/12?amount=1000") ~> Route.route ~> check {
            status shouldEqual StatusCodes.BadRequest
            responseAs[String] shouldEqual "Insufficient funds."
          }
        }
      }
    }
  }
}
