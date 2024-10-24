import sbt._

object Dependencies {
  private lazy val simexVersion = "0.9.3"
  private lazy val http4sVersion = "0.23.26"
  private lazy val catsEffectVersion = "3.5.4"

  lazy val all = Seq(
    "io.github.thediscprog" %% "simex-messaging" % simexVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-ember-client" % http4sVersion % Test,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "ch.qos.logback" % "logback-classic" % "1.5.11",
    "org.typelevel" %% "log4cats-core" % "2.7.0",
    "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test
  )
}
