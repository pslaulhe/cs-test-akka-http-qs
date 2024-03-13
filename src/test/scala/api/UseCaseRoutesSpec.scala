package api

import Database.SQL.SqlOrderRepository
import Providers.{InvoiceProviderImpl, PaymentProviderImpl, PricingProviderImpl, ShippingProviderImpl, StockProviderImpl}
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.api.JsonFormats.confirmCheckoutJsonFormat
import com.api.Request.ConfirmCheckoutRequest
import com.api.{UseCaseRegistry, UseCaseRoutes}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, times, verify}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.JsonParser
import useCases.ConfirmCheckoutUseCase

import java.util.UUID

class UseCaseRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  // so we have to adapt for now
  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[_] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe
  // created with testKit.createTestProbe()
  val userRegistry: ActorRef[UseCaseRegistry.Command] = testKit.spawn(UseCaseRegistry())
  val mockedConfirmCheckoutUseCase: ConfirmCheckoutUseCase = mock(classOf[ConfirmCheckoutUseCase])
  lazy val mockedUseCaseRoutes: Route = new UseCaseRoutes(userRegistry, mockedConfirmCheckoutUseCase).allRoutes

  val confirmCheckoutUseCase: ConfirmCheckoutUseCase = ConfirmCheckoutUseCase(StockProviderImpl(),PricingProviderImpl(),SqlOrderRepository(),PaymentProviderImpl(), InvoiceProviderImpl(), ShippingProviderImpl())
  lazy val useCaseRoutes: Route = new UseCaseRoutes(userRegistry, confirmCheckoutUseCase).allRoutes

  val confirmCheckoutRequestBody = "{\n    \"customerId\": 3,\n    \"customerEmailAddress\": \"fojadfs\",\n    \"shippingAddress\": {\n        \"id\": 5,\n        \"country\":\"CL\",\n        \"zipCode\": \"135\",\n        \"street\": \"streetSFOJ\",\n        \"number\": 135\n    },\n    \"creditCardInfo\": {\n        \"cardNumber\": \"43 857284723 54323\",\n        \"expirationDate\": \"2024-04-01\",\n        \"cvv\": \"123\",\n        \"cardholderName\": \"Pablo\"\n    },\n    \"productQuantities\": [[1, 3], [2, 7], [3, 1]]    \n}"
  val confirmCheckoutRequest: ConfirmCheckoutRequest = JsonParser(confirmCheckoutRequestBody).convertTo[ConfirmCheckoutRequest]

  "UseCaseRoutes" should {
    "be able to confirm checkout (POST /confirmCheckout)" in {
      val body = Marshal(confirmCheckoutRequest).to[MessageEntity].futureValue
      val request = Post(uri = "/confirmCheckout").withEntity(body)
      request ~> mockedUseCaseRoutes ~> check {
        verify(mockedConfirmCheckoutUseCase, times(1)).execute(any(), any(), any(), any(), any(), any())
        status should ===(StatusCodes.Created)

        // and we know what message we're expecting back:
        entityAs[String] should include("""{"description":"checkout created""")
      }
    }

    "confirm checkout and create order integration test (POST /confirmCheckout)" in {
      val body = Marshal(confirmCheckoutRequest).to[MessageEntity].futureValue
      val request = Post(uri = "/confirmCheckout").withEntity(body)
      request ~> useCaseRoutes ~> check {

        // Assert
        status should ===(StatusCodes.Created)

        // and we know what message we're expecting back:
        entityAs[String] should include("""{"description":"checkout created""")

        // and the order must've been created
        val orderRepo = SqlOrderRepository()
        val jsonString = entityAs[String]
        val regex = """[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}""".r

        val uuid = regex.findFirstIn(jsonString).getOrElse("")
        uuid should not be ""

        val order = orderRepo.get(UUID.fromString(uuid))
        order.productQuantities should be(Array.empty[(Int, Int)]) // the repo get method doesn't return product quantities
        order.shippingAddressId should be(5)
        order.customerId should be(3)
      }
    }
  }
}

