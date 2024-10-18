package io.github.thediscprog.simex.webservice.validation

import io.github.thediscprog.simexmessaging.messaging.Simex


trait SimexRequestValidatorAlgebra[F[_]] {

  def validateRequest(request: Simex): F[Validation]
}
