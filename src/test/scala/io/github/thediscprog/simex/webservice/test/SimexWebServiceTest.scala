package io.github.thediscprog.simex.webservice.test

import cats.effect.IO
import cats.effect.kernel.Async
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.github.thediscprog.simex.webservice.HttpRouteResource
import io.github.thediscprog.simex.webservice.services.{SimexMessageHandler, SimexMessageSecurityService}
import io.github.thediscprog.simex.webservice.validation.{SimexRequestValidatorAlgebra, Validation, ValidationPassed}
import io.github.thediscprog.simexmessaging.messaging.Simex
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.implicits._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger


class SimexWebServiceTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with SimexMessageFixture {

  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(30, Seconds), interval = Span(100, Millis))

  implicit val simexMessageDecoder: EntityDecoder[IO, Simex] = jsonOf[IO, Simex]
  implicit val simexMessageOkEncoder: EntityEncoder[IO, Simex] = jsonEncoderOf[IO, Simex]

  private case class AClass(value: String)
  private implicit val aClassEncoder: Encoder.AsObject[AClass] = deriveEncoder[AClass]
  private implicit val aClassEntityEncoder: EntityEncoder[IO, AClass] = jsonEncoderOf[IO, AClass]

  val URL = "simex-test"

  val httpApp = httpRoutes[IO].orNotFound

  val httpClient: Client[IO] = Client.fromHttpApp(httpApp)

  it should "return bad request" in {
    val badRequest: Request[IO] = Request(method = Method.GET, uri = uri"/bad-request")
    val resp = httpClient.status(badRequest).unsafeToFuture()

    whenReady(resp) { s =>
      s shouldBe Status.NotFound
    }
  }

  it should "return Unprocessable Entity when decoding fails" in {
    val badRequest: Request[IO] = Request(
      method = Method.POST,
      uri = uri"/simex-test"
    ).withEntity[AClass](AClass("test"))

    val response = httpClient.status(badRequest).unsafeToFuture()

    whenReady(response) { s =>
      s shouldBe Status.UnprocessableEntity
    }
  }

  it should "return No Content for a drop-off" in {
    val request: Request[IO] = Request(
      method = Method.POST,
      uri = uri"/simex-test"
    ).withEntity[Simex](simpleSimexMsg.copy(client = client.copy(requestId = "1")))

    val response = httpClient.status(request).unsafeToFuture()

    whenReady(response) { s =>
      s shouldBe Status.NoContent
    }
  }

  it should "return Service Unavailable" in {
    val request: Request[IO] = Request(
      method = Method.POST,
      uri = uri"/simex-test"
    ).withEntity[Simex](simpleSimexMsg.copy(client = client.copy(requestId = "2")))

    val response = httpClient.status(request).unsafeToFuture()

    whenReady(response) { s =>
      s shouldBe Status.ServiceUnavailable
    }
  }

  it should "return a simple Simex Message in OK response" in {
    val msgBody = simpleSimexMsg.copy(client = client.copy(requestId = "3"))
    val request: Request[IO] = Request(
      method = Method.POST,
      uri = uri"/simex-test"
    ).withEntity[Simex](msgBody)

    val response: IO[(Simex, Status)] = for {
      msg <- httpClient.expect[Simex](request)
      status <- httpClient.status(request)
    } yield (msg, status)

    whenReady(response.unsafeToFuture()) { r =>
      r._1 shouldBe msgBody
      r._2 shouldBe Status.Ok
    }
  }

  it should "return a more complex Simex Message in OK response" in {
    val msgBody = complexSimexMsg.copy(client = client.copy(requestId = "3"))
    val request: Request[IO] = Request(
      method = Method.POST,
      uri = uri"/simex-test"
    ).withEntity[Simex](msgBody)

    val response: IO[(Simex, Status)] = for {
      msg <- httpClient.expect[Simex](request)
      status <- httpClient.status(request)
    } yield (msg, status)

    whenReady(response.unsafeToFuture()) { r =>
      r._1 shouldBe msgBody
      r._2 shouldBe Status.Ok
    }
  }

  def httpRoutes[F[_]: Async: Logger]: HttpRoutes[F] = {
    //Security Service
    val securityService = new SimexMessageSecurityService[F]()

    //Validator
    val validator = new SimexRequestValidatorAlgebra[F] {
      override def validateRequest(request: Simex): F[Validation] =
        (ValidationPassed(): Validation).pure[F]
    }
    //Endpoint Handler
    val endpointHandler = new SimexMessageHandler[F](URL, securityService, validator)
    // Http Routes
    new HttpRouteResource[F]().routes(endpointHandler)
  }

  def checkResponse[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
      implicit ev: EntityDecoder[IO, A]
  ): Boolean = {
    val actualResponse = actual.unsafeRunSync()
    val statusCheck = actualResponse.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      actualResponse.body.compile.toVector.unsafeRunSync().isEmpty
    )(expected => actualResponse.as[A].unsafeRunSync() == expected)
    statusCheck && bodyCheck
  }
}
