package io.github.thediscprog.simex.webservice

import io.github.thediscprog.simexmessaging.messaging.Simex

object HttpResponseResource {

  sealed abstract class HttpResponse {
    def fold[A](
        handleOk: Simex => A,
        handleNoContent: => A,
        handleBadRequest: => A,
        handleForbiddenRequest: => A,
        handleServiceUnavailable: => A
    ): A = this match {
      case x: HttpResponse.Ok => handleOk(x.body)
      case HttpResponse.NoContent => handleNoContent
      case HttpResponse.BadRequest => handleBadRequest
      case HttpResponse.Forbidden => handleForbiddenRequest
      case HttpResponse.ServiceUnavailable => handleServiceUnavailable
    }
  }

  object HttpResponse {
    case class Ok(body: Simex) extends HttpResponse
    case object NoContent extends HttpResponse
    case object BadRequest extends HttpResponse
    case object Forbidden extends HttpResponse
    case object ServiceUnavailable extends HttpResponse
  }

}
