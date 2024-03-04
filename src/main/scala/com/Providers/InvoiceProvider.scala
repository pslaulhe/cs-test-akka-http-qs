package Providers

import Model.Order

trait InvoiceProvider {
  def sendInvoice(order: Order, emailAddress: String): Unit
}

case class InvoiceProviderImpl() extends InvoiceProvider {
  override def sendInvoice(order: Order, emailAddress: String): Unit = {
    println(s"Sending invoice to $emailAddress for order $order")
  }
}