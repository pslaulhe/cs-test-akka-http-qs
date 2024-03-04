package useCases

import Database.OrderRepository
import Model._
import Providers._

case class confirmCheckout(
      stockProvider: StockProvider,
      pricingProvider: PricingProvider,
      orderRepository: OrderRepository,
      paymentProvider: PaymentProvider,
      invoiceProvider: InvoiceProvider,
      shippingProvider: ShippingProvider)
{
  def execute(customerId:Int, customerEmailAddress:String, shippingAddress: Address, creditCardInfo: CreditCardInfo, productQuantities: Array[(Int, Int)]): Unit = {
    val order = Order(customerId, productQuantities, shippingAddress.id)

    val totalPrice: Double = getTotalPrice(productQuantities)
    orderRepository.create(order)

    paymentProvider.charge(creditCardInfo, totalPrice)
    invoiceProvider.sendInvoice(order, customerEmailAddress)
    shippingProvider.ship(order)
  }

  private def getTotalPrice(productQuantities: Array[(Int, Int)]) = {
    productQuantities.foldLeft(0.0)((priceAccumulator, productQuantity) => {
      val productStock = stockProvider.getStock(productQuantity._1)
      if (productStock < productQuantity._2) {
        throw new Exception("Not enough stock")
      }

      val productPrice = pricingProvider.getPrice(productQuantity._1)
      priceAccumulator + (productPrice * productQuantity._2)
    })
  }
}
