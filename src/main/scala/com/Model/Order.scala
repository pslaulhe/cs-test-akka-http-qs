package Model

case class Order(customerId: Int, productQuantities: Array[(Int, Int)], shippingAddressId: Int)
