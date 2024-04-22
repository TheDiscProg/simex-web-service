package simex.webservice.handler

import cats.Monad
import simex.messaging.Simex
import simex.webservice.HttpResponseResource
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.SimexMessageSecurityServiceAlgebra
import simex.webservice.validation.{
  SimexRequestValidatorAlgebra,
  ValidationFailed,
  ValidationPassed
}
import cats.syntax.all._
import simex.webservice.HttpResponseResource.HttpResponse

/** The HTTP web service handler for SIMEX message.
  * Each webservice handler should handle one and only one URL.
  * @param urlPath - the URL path for the web service that it handles
  * @param securityService - the security service implementation that checks the security
  * @tparam F - the monad wrapper for the return types
  */
abstract class SimexMessageHandlerAlgebra[F[_]: Monad](
    val urlPath: String,
    securityService: SimexMessageSecurityServiceAlgebra[F],
    requestValidator: SimexRequestValidatorAlgebra[F]
) {

  /** The actual handler that handles the SIMEX message.
    * @param respond - the response type
    * @param body - the HTTP request message body received
    * @return - the HTTP response
    */
  final def handleSimexRequest(request: Simex): F[HttpResponse] =
    for {
      securityCheck: SecurityResponse <- securityService.checkSecurityForRequest(request)
      response <- securityCheck match {
        case SecurityResponse.SecurityFailed =>
          HttpResponseResource.HttpResponse.Forbidden.pure[F]
        case SecurityResponse.SecurityPassed =>
          validateAndHandle(request)
      }
    } yield response

  final def validateAndHandle(request: Simex): F[HttpResponse] =
    for {
      validation <- requestValidator.validateRequest(request)
      response <- validation match {
        case ValidationFailed(_) =>
          HttpResponseResource.HttpResponse.BadRequest.pure[F]
        case ValidationPassed(_) =>
          handleValidatedSimexRequest(request)
      }
    } yield response

  def handleValidatedSimexRequest(request: Simex): F[HttpResponse]
}
