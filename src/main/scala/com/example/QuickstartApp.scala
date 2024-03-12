package com.example

import Database.SQL.SqlOrderRepository
import Providers._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.api.{UseCaseRegistry, UseCaseRoutes}

import scala.util.Failure
import scala.util.Success

//#main-class
object QuickstartApp {
  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor = context.spawn(UseCaseRegistry(), "UserRegistryActor")
      context.watch(userRegistryActor)

      val confirmCheckoutUseCase = buildConfirmCheckoutInstance();
      val routes = new UseCaseRoutes(userRegistryActor, confirmCheckoutUseCase)(context.system)
      startHttpServer(routes.allRoutes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
    //#server-bootstrapping
  }

  private def buildConfirmCheckoutInstance(): useCases.ConfirmCheckoutUseCase = {
    val invoiceProvider = InvoiceProviderImpl()
    val stockProvider = StockProviderImpl()
    val pricingProvider = PricingProviderImpl()
    val shippingProvider = ShippingProviderImpl()
    val paymentProvider = PaymentProviderImpl()
    val orderRepository = SqlOrderRepository()
    useCases.ConfirmCheckoutUseCase(stockProvider, pricingProvider, orderRepository, paymentProvider, invoiceProvider, shippingProvider)
  }
}
//#main-class
