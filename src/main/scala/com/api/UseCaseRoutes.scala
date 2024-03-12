package com.api

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.api.Request.ConfirmCheckoutRequest
import com.api.UseCaseRegistry.{ActionPerformed, ConfirmCheckout}
import useCases.ConfirmCheckoutUseCase

import java.time.Duration
import scala.concurrent.Future

//#import-json-formats
//#user-routes-class
class UseCaseRoutes(useCaseRegistry: ActorRef[UseCaseRegistry.Command], confirmCheckoutUseCase: ConfirmCheckoutUseCase)(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(Duration.ofSeconds(60))

  def confirmCheckout(request: ConfirmCheckoutRequest): Future[ActionPerformed] =
    useCaseRegistry.ask(ConfirmCheckout.apply(confirmCheckoutUseCase, _, request))

  //#all-routes
  //#users-get-post
  //#users-get-delete
  private val confirmCheckoutRoutes: Route =
    path("confirmCheckout") {
      post {
        entity(as[ConfirmCheckoutRequest]){ confirmCheckoutRequest =>
          onSuccess(confirmCheckout(confirmCheckoutRequest)) {
            performed => complete((StatusCodes.Created, performed))
          }
        }
      }
    }

  val allRoutes: Route = confirmCheckoutRoutes
  //#all-routes
}
