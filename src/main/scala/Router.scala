import scala.util.{Failure, Success}

import akka.http.scaladsl.server.{Directives, Route}

trait Router {
  def route: Route
}

class ClientRouter(clientRepository: ClientRepository) extends Router with Directives with ClientDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("clients") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(clientRepository.all()) { clients =>
          complete(clients)
        }
      } ~ post {
        entity(as[CreateClient]) { createClient =>
          validateWith(CreateClientValidator)(createClient) {
            handleWithGeneric(clientRepository.save(createClient)) { clients =>
              complete(clients)
            }
          }
        }
      }
    } 
  }
}
