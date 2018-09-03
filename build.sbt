import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtConfig

lazy val basicSettings = {
  val currentScalaVersion = "2.12.16"
  val scala211Version     = "2.11.11"

  Seq(
    organization := "Bounded Cargo Sample",
    description := "This is a sample application that makes use of the bounded framework for DDD, CQRS and Event Sourcing",
    scalaVersion := currentScalaVersion,
    crossScalaVersions := Seq(currentScalaVersion, scala211Version),
    //releaseCrossBuild := true,
    scalacOptions := Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.8",
      "-deprecation", // warning and location for usages of deprecated APIs
      "-feature", // warning and location for usages of features that should be imported explicitly
      "-unchecked", // additional warnings where generated code depends on assumptions
      "-Xlint:_,-missing-interpolator", // recommended additional warnings, disable missing-interpolator to prevent false warnings in swagger type info
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
      "-Ywarn-inaccessible",
      "-Ywarn-dead-code"
    ),
    headerLicense := {
      Some(HeaderLicense.Custom(
        s"Copyright (C) 2018 Creative Commons CC0 1.0 Universal"
      ))
    },
    scalastyleConfig := baseDirectory.value / "project/scalastyle-config.xml",
    scalafmtConfig := Some((baseDirectory in ThisBuild).value / "project/.scalafmt.conf"),
    scalafmtOnCompile := true
  )
}

lazy val root = (project in file("."))
  .settings(basicSettings: _*)
  .settings(publishArtifact := false,
    name := "Bounded Cargo Sample",
    libraryDependencies ++= Dependencies.baseDeps ++ Dependencies.persistanceLmdbDBDeps ++ Dependencies.persistenceLevelDBDeps
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "io.cafienne.bounded.cargosample",
    buildInfoOptions += BuildInfoOption.BuildTime,
    buildInfoOptions += BuildInfoOption.ToMap
  )
  .enablePlugins(AutomateHeaderPlugin)
