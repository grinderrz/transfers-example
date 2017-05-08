package transfers

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Directives._

object Route {
  val model = new Model
  val exceptionHandler = ExceptionHandler {
    case UserExistsException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
    case NoSuchUserException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
    case InsufficientFundsException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
  }

  val route = handleExceptions(exceptionHandler) {
    pathPrefix("user" / LongNumber) { id =>
      get {
        complete(model.getFunds(id).toString)
      } ~
      put {
        model.addUser(id)
        complete(StatusCodes.Created)
      } ~
      path("deposit") {
        post {
          parameters('amount.as[Int]) { amount =>
            model.deposit(id, amount)
            complete(StatusCodes.Created)
          }
        }
      } ~
      path("withdraw") {
        post {
          parameters('amount.as[Int]) { amount =>
            model.withdraw(id, amount)
            complete(StatusCodes.Created)
          }
        }
      } ~
      path("transfer" / LongNumber) { to =>
        post {
          parameters('amount.as[Int]) { amount =>
            model.transfer(id, to, amount)
            complete(StatusCodes.Created)
          }
        }
      }
    }
  }

}
