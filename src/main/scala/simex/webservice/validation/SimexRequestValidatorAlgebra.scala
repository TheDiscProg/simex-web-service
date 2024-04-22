package simex.webservice.validation

import simex.messaging.Simex

trait SimexRequestValidatorAlgebra[F[_]] {

  def validateRequest(request: Simex): F[Validation]
}
