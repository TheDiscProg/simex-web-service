package simex.webservice.handler

import simex.messaging.Simex
import simex.webservice.HttpResponseResource
import simex.webservice.security.SimexMessageSecurityServiceAlgebra

/** The HTTP web service handler for SIMEX message.
  * Each webservice handler should handle one and only one URL.
  * @param urlPath - the URL path for the web service that it handles
  * @param securityService - the security service implementation that checks the security
  * @tparam F - the monad wrapper for the return types
  */
abstract class SimexMessageHandlerAlgebra[F[_]](
    val urlPath: String,
    securityService: SimexMessageSecurityServiceAlgebra[F]
) {

  /** The actual handler that handles the SIMEX message.
    * @param respond - the response type
    * @param body - the HTTP request message body received
    * @return - the HTTP response
    */
  def handleSimexRequest(respond: HttpResponseResource.HttpResponse.type)(
      body: Simex
  ): F[HttpResponseResource.HttpResponse]
}
