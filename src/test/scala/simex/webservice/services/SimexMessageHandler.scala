package simex.webservice.services

import cats._
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import simex.messaging.Simex
import simex.webservice.HttpResponseResource
import simex.webservice.HttpResponseResource.HttpResponse
import simex.webservice.handler.SimexMessageHandlerAlgebra
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.SimexMessageSecurityServiceAlgebra
import simex.webservice.validation.SimexRequestValidatorAlgebra

class SimexMessageHandler[F[_]: Monad: Logger](
    override val urlPath: String,
    securityService: SimexMessageSecurityServiceAlgebra[F],
    validator: SimexRequestValidatorAlgebra[F]
) extends SimexMessageHandlerAlgebra[F](urlPath, securityService, validator) {

  override def handleValidatedSimexRequest(request: Simex): F[HttpResponse] =
    for {
      _ <- Logger[F].info(s"SimexMessageHandler - handling [$request]")
      securityCheck <- securityService.checkSecurityForRequest(request)
      response <- securityCheck match {
        case SecurityResponse.SecurityPassed => handleSimexMessage(request)
        case SecurityResponse.SecurityFailed => HttpResponse.Forbidden.pure[F]
      }
    } yield response

  private def handleSimexMessage(simex: Simex): F[HttpResponseResource.HttpResponse] =
    (simex.client.requestId match {
      case "1" => HttpResponse.NoContent: HttpResponse
      case "2" => HttpResponse.ServiceUnavailable: HttpResponse
      case "3" => HttpResponse.Ok(simex): HttpResponse
      case _ => HttpResponse.BadRequest: HttpResponse
    }).pure[F]

}
