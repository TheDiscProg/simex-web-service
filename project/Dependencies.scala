import sbt._

object Dependencies {
  private lazy val simexVersion = "0.9.2"
  private lazy val http4sVersion = "0.23.26"
  private lazy val catsEffectVersion = "3.5.4"
  private lazy val circeVersion = "0.14.10"
  private lazy val enumeratumVersion = "1.7.5"

  lazy val all = Seq(
    "io.github.thediscprog" %% "simex-messaging" % simexVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "org.http4s" %% "http4s-dsl" % http4sVersion,
    "org.http4s" %% "http4s-ember-server" % http4sVersion,
    "org.http4s" %% "http4s-ember-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "io.circe" %% "circe-refined" % "0.15.1",
    "io.circe" %% "circe-config" % "0.10.1",
    "eu.timepit" %% "refined" % "0.11.2",
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-circe" % enumeratumVersion,
    "io.scalaland" %% "chimney" % "0.8.4",
    "ch.qos.logback" % "logback-classic" % "1.5.11",
    "org.typelevel" %% "log4cats-core" % "2.7.0",
    "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    "org.scalactic" %% "scalactic" % "3.2.19",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.typelevel" %% "munit-cats-effect-2" % "1.0.7" % Test,
    "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % Test
  )
}
