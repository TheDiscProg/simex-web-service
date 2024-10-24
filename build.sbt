import sbt.librarymanagement.CrossVersion
import sbt.url
import xerial.sbt.Sonatype.{GitHubHosting, sonatypeCentralHost}

lazy val scala2 = "2.13.15"
lazy val scala3 = "3.5.1"
lazy val supportedScalaVersions = List(scala2, scala3)

lazy val commonSettings = Seq(
  scalaVersion := scala3,
  libraryDependencies ++= Dependencies.all
)

lazy val root = (project in file("."))
  .enablePlugins(
    ScalafmtPlugin
  )
  .settings(
    commonSettings,
    name := "simex-web-service",
    scalacOptions ++= Scalac.options,
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2,13)) => Seq("-Ytasty-reader")
        case _ => Seq("-Yretain-trees")
      }
    },
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= {
      if (scalaVersion.value.startsWith("2"))
        Seq(
          compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.3").cross(CrossVersion.full)),
          compilerPlugin(("com.olegpy" %% "better-monadic-for" % "0.3.1"))
        )
      else
        Seq.empty
    }
  )

ThisBuild / version := "0.9.3"
ThisBuild / organization := "io.github.thediscprog"
ThisBuild / organizationName := "thediscprog"
ThisBuild / organizationHomepage := Some(url("https://github.com/TheDiscProg"))

ThisBuild / description := "Web Service for handling Simex messages using HTTP POST"

// Sonatype/Maven Publishing
ThisBuild / publishMavenStyle := true
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeProfileName := "io.github.thediscprog"
ThisBuild / licenses := List("GNU-3.0" -> url("https://www.gnu.org/licenses/gpl-3.0.en.html"))
ThisBuild / homepage := Some(url("https://github.com/TheDiscProg/simex-web-service"))
ThisBuild / sonatypeProjectHosting := Some(GitHubHosting("TheDiscProg", "simex-web-service", "TheDiscProg@gmail.com"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TheDiscProg/simex-web-service"),
    "scm:git@github.com:thediscprog/simex-web-service.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "thediscprog",
    name = "TheDiscProg",
    email = "TheDiscProg@gmail.com",
    url = url("https://github.com/TheDiscProg")
  )
)

usePgpKeyHex("FC6901A47E5DA2533DCF25D51615DCC33B57B2BF")

sonatypeCredentialHost := "central.sonatype.com"
sonatypeRepository := "https://central.sonatype.com/api/v1/publisher/"

ThisBuild / versionScheme := Some("early-semver")

addCommandAlias("formatAll", ";scalafmt;test:scalafmt;")
addCommandAlias("cleanTest", ";clean;scalafmt;test:scalafmt;test;")
addCommandAlias("cleanCoverage", ";formatAll;coverage;cleanTest;coverageReport;")
