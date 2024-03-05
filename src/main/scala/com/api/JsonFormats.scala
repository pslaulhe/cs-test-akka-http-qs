package com.api

import Model.{Address, CreditCardInfo}
import UseCaseRegistry.ActionPerformed
import com.api.Request.ConfirmCheckoutRequest
import spray.json.{JsString, JsValue}
import java.time.LocalDate

//#json-formats
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol

object JsonFormats {
  // import the default encoders for primitive types (Int, String, Lists etc)

  import DefaultJsonProtocol._

  implicit val noneJsonFormat: RootJsonFormat[None.type] = jsonFormat0(() => None)
  implicit val localDateJsonFormat: RootJsonFormat[LocalDate] = new RootJsonFormat[LocalDate] {
    override def write(obj: LocalDate): JsValue = JsString(obj.toString)

    override def read(json: JsValue): LocalDate = LocalDate.parse(json.convertTo[String])
  }
  implicit val addressJsonFormat: RootJsonFormat[Address] = jsonFormat5(Address.apply)
  implicit val creditCardInfoJsonFormat: RootJsonFormat[CreditCardInfo] = jsonFormat4(CreditCardInfo.apply)
  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed]  = jsonFormat1(ActionPerformed.apply)

  implicit val confirmCheckoutJsonFormat: RootJsonFormat[ConfirmCheckoutRequest] = jsonFormat5(ConfirmCheckoutRequest.apply)
}
//#json-formats
