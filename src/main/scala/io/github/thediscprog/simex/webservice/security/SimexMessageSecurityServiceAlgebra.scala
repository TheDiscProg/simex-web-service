package io.github.thediscprog.simex.webservice.security

import SecurityResponseResource.SecurityResponse
import io.github.thediscprog.simexmessaging.messaging.Simex

trait SimexMessageSecurityServiceAlgebra[F[_]] {

  def checkSecurityForRequest(request: Simex): F[SecurityResponse]

}
