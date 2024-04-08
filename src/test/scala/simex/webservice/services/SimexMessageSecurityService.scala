package simex.webservice.services

import cats.Applicative
import cats.syntax.all._
import simex.messaging.Simex
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.{SecurityResponseResource, SimexMessageSecurityServiceAlgebra}

class SimexMessageSecurityService[F[_]: Applicative] extends SimexMessageSecurityServiceAlgebra[F] {

  override def handleSimexRequest(
      respond: SecurityResponseResource.SecurityResponse.type
  )(body: Simex): F[SecurityResponse] =
    if (body.client.authorization != "securitytoken")
      (SecurityResponseResource.SecurityResponse.SecurityFailed: SecurityResponse).pure[F]
    else
      (SecurityResponseResource.SecurityResponse.SecurityPassed: SecurityResponse).pure[F]
}
