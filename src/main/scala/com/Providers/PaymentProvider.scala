package Providers

import Model.CreditCardInfo

trait PaymentProvider {
  def charge(creditCardInfo: CreditCardInfo, amount: Double): Boolean
}


case class PaymentProviderImpl() extends PaymentProvider {
  override def charge(creditCardInfo: CreditCardInfo, amount: Double): Boolean = {
    println(s"Charging ${creditCardInfo} for amount ${amount}")
    true
  }
}