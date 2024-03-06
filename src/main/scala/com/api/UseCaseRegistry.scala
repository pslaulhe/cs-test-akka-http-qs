package com.api

//#user-registry-actor

import Database.SQL.SqlOrderRepository
import Providers._
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.api.Request.ConfirmCheckoutRequest
import useCases.confirmCheckout

object UseCaseRegistry {
  // actor protocol
  sealed trait Command
  final case class ConfirmCheckout(replyTo: ActorRef[ActionPerformed], request: ConfirmCheckoutRequest) extends Command

  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry()

  private val invoiceProvider = InvoiceProviderImpl()
  private val stockProvider = StockProviderImpl()
  private val pricingProvider = PricingProviderImpl()
  private val shippingProvider = ShippingProviderImpl()
  private val paymentProvider = PaymentProviderImpl()

  private val orderRepository = SqlOrderRepository()

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case ConfirmCheckout(replyTo, request) =>
        val checkoutUseCase = confirmCheckout(stockProvider, pricingProvider, orderRepository, paymentProvider, invoiceProvider, shippingProvider)
        checkoutUseCase.execute(request.customerId, request.customerEmailAddress, request.shippingAddress, request.creditCardInfo, request.productQuantities)
        replyTo ! ActionPerformed("checkout created")
        Behaviors.same
    }
}
//#user-registry-actor
