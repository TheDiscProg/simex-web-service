package simex.webservice.services

import cats.Applicative
import cats.syntax.all._
import simex.messaging.Simex
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.{SecurityResponseResource, SimexMessageSecurityServiceAlgebra}

class SimexMessageSecurityService[F[_]: Applicative] extends SimexMessageSecurityServiceAlgebra[F] {

  override def checkSecurityForRequest(request: Simex): F[SecurityResponse] =
    if (request.client.authorization != "securitytoken")
      (SecurityResponseResource.SecurityResponse.SecurityFailed: SecurityResponse).pure[F]
    else
      (SecurityResponseResource.SecurityResponse.SecurityPassed: SecurityResponse).pure[F]
}
