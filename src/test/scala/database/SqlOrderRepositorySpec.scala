package database

import Database.SQL.SqlOrderRepository
import Model.Order
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SqlOrderRepositorySpec extends AnyWordSpec with Matchers {

  "SqlOrderRepository" should {
    val sqlOrderRepository = SqlOrderRepository()
    "create a new order" in {
      val order = Order(java.util.UUID.randomUUID, 1, Array(1 -> 1, 2 -> 3), 1)
      sqlOrderRepository.create(order)
      sqlOrderRepository.get(order.orderUid).orderUid should be(order.orderUid)
    }
  }
}
