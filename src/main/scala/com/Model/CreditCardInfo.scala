package Model

import java.time.LocalDate

case class CreditCardInfo(cardNumber: String, expirationDate: LocalDate, cvv: String, cardholderName: String)
