import play.core.server._
import play.api.routing.sird._
import play.api.mvc._  

object HttpServer extends App {
  println("program started...")
  
  val server = NettyServer.fromRouter() {
    case GET(p"/hello/") => Action {
      Results.Ok(s"Hello")
    }
  }
}
