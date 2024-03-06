package Database.SQL

import Database.OrderRepository
import Model.Order

import java.sql.{Connection, ResultSet}
import java.util.UUID

case class SqlOrderRepository() extends OrderRepository {
  private val sqlProvider: SqlProvider = SqlProvider()
  private val insertOrderStatement = "INSERT INTO orders(id, customerId, shippingAddressId) VALUES (?, ?, ?)"
  private val insertProductQuantityStatement = "INSERT INTO productQuantity(orderId, productId, quantity) VALUES (?, ?, ?)"
  private val getOrderStatement = "SELECT * FROM orders o WHERE o.id = ?"

  override def create(order: Order): Unit = {
    this.sqlProvider.executeCommand(createOrder(_, order))
  }

  override def get(orderId: UUID): Order = {
    val orders = this.sqlProvider.get(getOrder(_, orderId))(resultSet =>
      Order(resultSet.getObject("id", classOf[java.util.UUID]), resultSet.getInt("customerId"), Array.empty[(Int, Int)], resultSet.getInt("shippingAddressId"))
    )

    if (!orders.hasNext) throw new NoSuchElementException("No order found")
    orders.next()
  }

  private def getOrder(connection: Connection, orderId: UUID): ResultSet = {
    val statement = connection.prepareStatement(getOrderStatement)
    statement.setObject(1, orderId)
    statement.executeQuery()
  }

  private def createOrder(connection: Connection, order: Order): Unit = {
    val statement = connection.prepareStatement(insertOrderStatement)
    statement.setObject(1, order.orderUid)
    statement.setInt(2, order.customerId)
    statement.setInt(3, order.shippingAddressId)
    statement.executeUpdate()

    createProductQuantities(order.orderUid, order, connection)
  }

  private def createProductQuantities(orderId: UUID, order: Order, connection: Connection): Unit = {
    order.productQuantities.foreach(product => {
      val productQuantityStatement = connection.prepareStatement(insertProductQuantityStatement)
      productQuantityStatement.setObject(1, orderId)
      productQuantityStatement.setInt(2, product._1)
      productQuantityStatement.setInt(3, product._2)
      productQuantityStatement.executeUpdate()
    })
  }
}
