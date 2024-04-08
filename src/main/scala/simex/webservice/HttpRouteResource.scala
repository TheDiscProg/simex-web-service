package simex.webservice

import cats.Monad
import cats.arrow.FunctionK
import cats.effect.Async
import cats.implicits._
import org.http4s.EntityEncoder.stringEncoder
import org.http4s.circe.CirceInstances
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.EntityResponseGenerator
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response, Status}
import simex.messaging.Simex
import simex.webservice.handler.SimexMessageHandlerAlgebra

class HttpRouteResource[F[_]: Monad](
    mapRoute: (String, Request[F], F[Response[F]]) => F[Response[F]] =
      (_: String, _: Request[F], r: F[Response[F]]) => r
)(implicit F: Async[F])
    extends Http4sDsl[F]
    with CirceInstances {
  import HttpResponseResource._

  private val simexMessageDecoder: EntityDecoder[F, Simex] = jsonOf[F, Simex]
  private val simexMessageOkEncoder = jsonEncoderOf[F, Simex]
  private val simexMessageOkEntityResponseGenerator = new EntityResponseGenerator[F, F] {
    override def liftG: FunctionK[F, F] = cats.arrow.FunctionK.id

    override def status: Status = Status.Ok
  }

  def routes(handler: SimexMessageHandlerAlgebra[F]): HttpRoutes[F] = HttpRoutes.of {
    { case req @ POST -> Root / handler.urlPath =>
      mapRoute(
        "simexRequest",
        req,
        req
          .attemptAs(simexMessageDecoder)
          .foldF(
            err =>
              err.cause match {
                case Some(circeErr: io.circe.DecodingFailure) =>
                  Response[F](
                    status = org.http4s.Status.UnprocessableEntity,
                    body = stringEncoder
                      .toEntity(
                        "The request body was invalid. " + circeErr.message + ": " + circeErr.history
                          .mkString(", ")
                      )
                      .body
                  ).pure[F]
                case _ =>
                  err.toHttpResponse[F](req.httpVersion).pure[F]
              },
            body =>
              handler
                .handleSimexRequest(HttpResponse)(body)
                .flatMap({
                  case b: HttpResponse.Ok =>
                    simexMessageOkEntityResponseGenerator(b.body)(F, simexMessageOkEncoder)
                  case HttpResponse.NoContent =>
                    F.pure(Response[F](status = org.http4s.Status.NoContent))
                  case HttpResponse.BadRequest =>
                    F.pure(Response[F](status = org.http4s.Status.BadRequest))
                  case HttpResponse.Forbidden =>
                    F.pure(Response[F](status = org.http4s.Status.Forbidden))
                  case HttpResponse.ServiceUnavailable =>
                    F.pure(Response[F](status = org.http4s.Status.ServiceUnavailable))
                })
          )
      )
    }
  }
}
