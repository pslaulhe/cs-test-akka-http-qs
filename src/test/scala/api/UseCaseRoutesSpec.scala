package api

import akka.actor.Actor
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{MessageEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.api.JsonFormats.confirmCheckoutJsonFormat
import com.api.Request.ConfirmCheckoutRequest
import com.api.{UseCaseRegistry, UseCaseRoutes}
import com.example.JsonFormats
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, times, verify, when}
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.JsonParser
import useCases.ConfirmCheckoutUseCase

import scala.concurrent.Future

class UseCaseRoutesSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  // so we have to adapt for now
  lazy val testKit: ActorTestKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[_] = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.classicSystem

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  // Here we need to implement all the abstract members of UserRoutes.
  // We use the real UserRegistryActor to test it while we hit the Routes,
  // but we could "mock" it by implementing it in-place or by using a TestProbe
  // created with testKit.createTestProbe()
  val userRegistry: ActorRef[UseCaseRegistry.Command] = testKit.spawn(UseCaseRegistry())
  val confirmCheckoutUseCase: ConfirmCheckoutUseCase = mock(classOf[ConfirmCheckoutUseCase])
  lazy val routes: Route = new UseCaseRoutes(userRegistry, confirmCheckoutUseCase).allRoutes

  val confirmCheckoutRequestBody = "{\n    \"customerId\": 3,\n    \"customerEmailAddress\": \"fojadfs\",\n    \"shippingAddress\": {\n        \"id\": 5,\n        \"country\":\"CL\",\n        \"zipCode\": \"135\",\n        \"street\": \"streetSFOJ\",\n        \"number\": 135\n    },\n    \"creditCardInfo\": {\n        \"cardNumber\": \"43 857284723 54323\",\n        \"expirationDate\": \"2024-04-01\",\n        \"cvv\": \"123\",\n        \"cardholderName\": \"Pablo\"\n    },\n    \"productQuantities\": [[1, 3], [2, 7], [3, 1]]    \n}"
  val confirmCheckoutRequest: ConfirmCheckoutRequest = JsonParser(confirmCheckoutRequestBody).convertTo[ConfirmCheckoutRequest]

  "UseCaseRoutes" should {
    "be able to confirm checkout (POST /confirmCheckout)" in {
      val body = Marshal(confirmCheckoutRequest).to[MessageEntity].futureValue

      // Stub/mock the behavior of confirmCheckoutUseCase.execute to return a successful future
      when(confirmCheckoutUseCase.execute(any(), any(), any(), any(), any()))
        .thenReturn(Future.successful(())) // Assuming execute method returns a Future[Unit]

      val request = Post(uri = "/confirmCheckout").withEntity(body)
      request ~> routes ~> check {
        verify(confirmCheckoutUseCase, times(1)).execute(any(), any(), any(), any(), any())
        status should ===(StatusCodes.Created)

        // and we know what message we're expecting back:
        entityAs[String] should ===("""{"description":"checkout created"}""")
      }
    }
  }
}
