package io.github.thediscprog.simex.webservice.services

import cats.Applicative
import cats.syntax.all._
import io.github.thediscprog.simex.webservice.security.{
  SecurityResponseResource,
  SimexMessageSecurityServiceAlgebra
}
import io.github.thediscprog.simex.webservice.security.SecurityResponseResource.SecurityResponse
import io.github.thediscprog.simexmessaging.messaging.Simex

class SimexMessageSecurityService[F[_]: Applicative] extends SimexMessageSecurityServiceAlgebra[F] {

  override def checkSecurityForRequest(request: Simex): F[SecurityResponse] =
    if (request.client.authorization != "securitytoken")
      (SecurityResponseResource.SecurityResponse.SecurityFailed: SecurityResponse).pure[F]
    else
      (SecurityResponseResource.SecurityResponse.SecurityPassed: SecurityResponse).pure[F]
}
