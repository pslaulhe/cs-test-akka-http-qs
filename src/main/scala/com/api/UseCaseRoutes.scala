package com.api

import Model.{Address, CreditCardInfo}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.Uri.Path.~
import akka.util.Timeout
import UseCaseRegistry.{ActionPerformed, ConfirmCheckout}

import java.util.concurrent.TimeUnit
import java.time.Duration
import java.time.temporal.TemporalUnit

//#import-json-formats
//#user-routes-class
class UseCaseRoutes(useCaseRegistry: ActorRef[UseCaseRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  //#import-json-formats
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(Duration.ofSeconds(60))

  def confirmCheckout(customerId:Int, customerEmailAddress:String, shippingAddress: Address, creditCardInfo: CreditCardInfo, productQuantities: Array[(Int, Int)]): Future[ActionPerformed] =
    useCaseRegistry.ask(ConfirmCheckout.apply)

  //#all-routes
  //#users-get-post
  //#users-get-delete
  val confirmCheckoutRoutes: Route =
    path("confirmCheckout") {
      post {
        parameters("customerId" ? 0, "customerEmailAddress".as[String], "shippingAddress".as[Address], "creditCardInfo".as[CreditCardInfo]) {
          (customerId, customerEmailAddress, shippingAddress, creditCardInfo) =>
            entity(as[Array[(Int, Int)]]) { productQuantities =>
              onSuccess(confirmCheckout(customerId, customerEmailAddress, shippingAddress, creditCardInfo, productQuantities)) { performed =>
                complete((StatusCodes.Created, performed))
              }
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
