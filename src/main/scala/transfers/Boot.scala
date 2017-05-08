package transfers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import com.typesafe.config.ConfigFactory

object Route {
  val model = new Model
  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case UserExistsException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
    case NoSuchUserException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
    case InsufficientFundsException(message) =>
      complete(HttpResponse(StatusCodes.BadRequest, entity = message))
  }

  val route: Route = handleExceptions(exceptionHandler) {
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

object Boot extends App {
  private val config = ConfigFactory.load()
  implicit val system = ActorSystem("TransfersSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val port = config.getInt("boot.port")
  val interface = config.getString("boot.interface")

  val bindingFuture = Http().bindAndHandle(Route.route, interface, port)

  StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
}
