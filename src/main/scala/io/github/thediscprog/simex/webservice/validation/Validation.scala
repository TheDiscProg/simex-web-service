package io.github.thediscprog.simex.webservice.validation

sealed trait Validation {
  val status: String
}

case class ValidationPassed(val status: String = "Request Validated") extends Validation
case class ValidationFailed(val status: String) extends Validation
