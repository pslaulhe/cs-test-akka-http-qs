package com.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import UseCaseRegistry.{ActionPerformed, ConfirmCheckout}
import com.api.Request.ConfirmCheckoutRequest
import java.time.Duration

//#import-json-formats
//#user-routes-class
class UseCaseRoutes(useCaseRegistry: ActorRef[UseCaseRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(Duration.ofSeconds(60))

  def confirmCheckout(request: ConfirmCheckoutRequest): Future[ActionPerformed] =
    useCaseRegistry.ask(ConfirmCheckout.apply(_, request))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val confirmCheckoutRoutes: Route =
    path("confirmCheckout") {
      post {
        entity(as[ConfirmCheckoutRequest]){ confirmCheckoutRequest =>
          onSuccess(confirmCheckout(confirmCheckoutRequest)) {
            performed => complete((StatusCodes.Created, performed))
          }
        }
      }
    }

  val helloRoute: Route =
    path("hello") {
      get {
        complete("Hello, World!")
      }
    }

  val allRoutes: Route = confirmCheckoutRoutes ~ helloRoute
  //#all-routes
}
