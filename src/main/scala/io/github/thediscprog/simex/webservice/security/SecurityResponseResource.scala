package io.github.thediscprog.simex.webservice.security

object SecurityResponseResource {

  sealed abstract class SecurityResponse {
    def fold[A](
        handleSecurityPassed: => A,
        handleSecurityFailed: => A
    ): A = this match {
      case SecurityResponse.SecurityPassed => handleSecurityPassed
      case SecurityResponse.SecurityFailed => handleSecurityFailed
    }
  }

  object SecurityResponse {
    case object SecurityPassed extends SecurityResponse
    case object SecurityFailed extends SecurityResponse
  }

}
