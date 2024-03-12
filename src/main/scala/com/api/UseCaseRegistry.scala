package com.api

//#user-registry-actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.api.Request.ConfirmCheckoutRequest
import useCases.ConfirmCheckoutUseCase

object UseCaseRegistry {
  // actor protocol
  sealed trait Command
  final case class ConfirmCheckout(confirmCheckoutUseCase: ConfirmCheckoutUseCase, replyTo: ActorRef[ActionPerformed], request: ConfirmCheckoutRequest) extends Command

  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry()

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case ConfirmCheckout(confirmCheckoutUseCase, replyTo, request) =>
        confirmCheckoutUseCase.execute(request.customerId, request.customerEmailAddress, request.shippingAddress, request.creditCardInfo, request.productQuantities)
        replyTo ! ActionPerformed("checkout created")
        Behaviors.same
    }
}
//#user-registry-actor
