package com.api

//#user-registry-actor

import Database.OrderRepositoryImpl
import Providers._
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.immutable
import useCases.confirmCheckout

object UseCaseRegistry {
  // actor protocol
  sealed trait Command
  final case class ConfirmCheckout(replyTo: ActorRef[ActionPerformed]) extends Command

  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry()

  private val invoiceProvider = InvoiceProviderImpl()
  private val stockProvider = StockProviderImpl()
  private val pricingProvider = PricingProviderImpl()
  private val shippingProvider = ShippingProviderImpl()
  private val paymentProvider = PaymentProviderImpl()

  private val orderRepository = OrderRepositoryImpl()

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case ConfirmCheckout(replyTo) =>
        val checkoutUseCase = confirmCheckout(stockProvider, pricingProvider, orderRepository, paymentProvider, invoiceProvider, shippingProvider)
          // replyTo ! checkoutUseCase.execute()
        Behaviors.same
    }
}
//#user-registry-actor
