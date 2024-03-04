package Providers

trait StockProvider {
  def getStock(productId: Int): Int
}

case class StockProviderImpl() extends StockProvider {
  override def getStock(productId: Int): Int = {
    productId match {
      case 1 => 10
      case 2 => 20
      case 3 => 30
      case 4 => 40
      case 5 => 50
      case _ => 0
    }
  }
}