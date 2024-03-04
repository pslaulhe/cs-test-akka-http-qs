package Providers

import Model.Order

trait ShippingProvider {
  def ship(order: Order): Unit
}

case class ShippingProviderImpl() extends ShippingProvider {
  override def ship(order: Order): Unit = {
    println(s"Shipping order ${order.shippingAddressId}")
  }
}