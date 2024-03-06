package Model

import java.util.UUID

case class Order(orderUid: UUID, customerId:  Int, productQuantities: Array[(Int, Int)], shippingAddressId: Int)
