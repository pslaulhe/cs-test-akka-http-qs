package Database

import Model.Order

trait OrderRepository {
  def create(order: Order): Unit
}

case class OrderRepositoryImpl() extends OrderRepository {
  override def create(order: Order): Unit = {
    println(s"Order created: $order")
  }
}