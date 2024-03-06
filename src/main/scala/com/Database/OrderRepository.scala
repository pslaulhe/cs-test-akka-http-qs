package Database

import Model.Order

import java.util.UUID

trait OrderRepository {
  def create(order: Order): Unit
  def get(orderId: UUID): Order
}