package Providers

trait PricingProvider {
  def getPrice(productId: Int): Double
}

case class PricingProviderImpl() extends PricingProvider {
  override def getPrice(productId: Int): Double = {
    productId match {
      case 1 => 100.0
      case 2 => 200.0
      case 3 => 300.0
      case 4 => 400.0
      case 5 => 500.0
      case _ => 0.0
    }
  }
}