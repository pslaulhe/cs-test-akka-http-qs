package Database.SQL

import Database.OrderRepository
import Model.Order

import java.sql.{Connection, PreparedStatement}

case class SqlOrderRepository() extends OrderRepository {
  private val sqlProvider: SqlProvider = SqlProvider()
  private val insertOrderStatement = "INSERT INTO order(customerId, shippingAddressId) VALUES (?, ?)"
  private val insertProductQuantityStatement = "INSERT INTO productQuantity(orderId, productId, quantity) VALUES (?, ?, ?)"

  override def create(order: Order): Unit = {
    this.sqlProvider.executeCommand(createOrder(_, order))
  }

  private def createOrder(connection: Connection, order: Order): Unit = {
    val statement = connection.prepareStatement(insertOrderStatement)
    statement.setInt(1, order.customerId)
    statement.setInt(2, order.shippingAddressId)
    statement.executeUpdate()

    val orderId = statement.getGeneratedKeys.getInt(1)
    createProductQuantities(orderId, order, statement, connection)
  }

  private def createProductQuantities(orderId: Int, order: Order, statement: PreparedStatement, connection: Connection): Unit = {
    order.productQuantities.foreach(product => {
      val productQuantityStatement = connection.prepareStatement(insertProductQuantityStatement)
      productQuantityStatement.setInt(1, orderId)
      productQuantityStatement.setInt(2, product._1)
      productQuantityStatement.setInt(3, product._2)
      productQuantityStatement.executeUpdate()
    })
  }
}
