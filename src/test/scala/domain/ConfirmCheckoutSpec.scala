package domain

import Database.SQL.SqlOrderRepository
import Model.{Address, CreditCardInfo, Order}
import Providers._
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import useCases.ConfirmCheckoutUseCase

import java.time.LocalDate;

class ConfirmCheckoutSpec extends AnyWordSpec with Matchers {
  "ConfirmCheckout" should {
    val stockProvider: StockProvider = mock(classOf[StockProviderImpl])
    val pricingProvider: PricingProvider = mock(classOf[PricingProviderImpl])
    val orderRepository: SqlOrderRepository = mock(classOf[SqlOrderRepository])
    val paymentProvider: PaymentProvider = mock(classOf[PaymentProviderImpl])
    val invoiceProvider: InvoiceProvider = mock(classOf[InvoiceProviderImpl])
    val shippingProvider: ShippingProvider = mock(classOf[ShippingProviderImpl])

    val confirmCheckoutUseCase = ConfirmCheckoutUseCase(stockProvider, pricingProvider, orderRepository, paymentProvider, invoiceProvider, shippingProvider)

    "test execute happy path" in {
      val address = Address(1, "country", "", "", 12)
      val creditCardInfo = CreditCardInfo("testNumber", LocalDate.now(), "123", "cardHolderName")
      val orderToBeCreated = Order(java.util.UUID.randomUUID, 1, Array((1, 1)), 1)

      when(stockProvider.getStock(1)).thenReturn(10)
      when(pricingProvider.getPrice(1)).thenReturn(50.0)

      val productQuantities = Array((1, 1))
      confirmCheckoutUseCase.execute(1, "testEmail", address, creditCardInfo, productQuantities)

      // Assert
      verify(stockProvider).getStock(1)
      verify(pricingProvider).getPrice(1)
      verify(pricingProvider, never()).getPrice(2)
      verify(paymentProvider, times(1)).charge(any(), any())
      verify(invoiceProvider, times(1)).sendInvoice(any(), any())

      val argumentCaptor = ArgumentCaptor.forClass(classOf[Order])

      verify(shippingProvider, times(1)).ship(argumentCaptor.capture())
      val shipOrderArgumentValue = argumentCaptor.getValue.asInstanceOf[Order]
      orderToBeCreated.customerId should be(shipOrderArgumentValue.customerId)
      orderToBeCreated.productQuantities should be(shipOrderArgumentValue.productQuantities)
      orderToBeCreated.shippingAddressId should be(shipOrderArgumentValue.shippingAddressId)

      verify(orderRepository, times(1)).create(argumentCaptor.capture())
      val createOrderArgumentValue = argumentCaptor.getValue.asInstanceOf[Order]
      orderToBeCreated.customerId should be(createOrderArgumentValue.customerId)
      orderToBeCreated.productQuantities should be(createOrderArgumentValue.productQuantities)
      orderToBeCreated.shippingAddressId should be(createOrderArgumentValue.shippingAddressId)
    }
  }
}
