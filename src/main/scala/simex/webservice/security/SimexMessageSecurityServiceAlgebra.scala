package simex.webservice.security

import simex.messaging.Simex
import simex.webservice.security.SecurityResponseResource.SecurityResponse

trait SimexMessageSecurityServiceAlgebra[F[_]] {

  def handleSimexRequest(respond: SecurityResponseResource.SecurityResponse.type)(
      body: Simex
  ): F[SecurityResponse]

}
