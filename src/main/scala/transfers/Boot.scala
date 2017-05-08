package transfers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import scala.io.StdIn
import com.typesafe.config.ConfigFactory

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
