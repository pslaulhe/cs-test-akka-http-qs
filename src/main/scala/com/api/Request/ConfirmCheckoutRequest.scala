package com.api.Request

import Model.{Address, CreditCardInfo}

case class ConfirmCheckoutRequest(customerId:Int, customerEmailAddress:String, shippingAddress: Address, creditCardInfo: CreditCardInfo, productQuantities: Array[(Int, Int)])
