name := "cargo-sample"

organization := "io.cafienne"

description := "This is a sample application that makes use of the bounded framework for DDD, CQRS and Event Sourcing"

version := "0.1"

scalaVersion := "2.13.4"

scalastyleConfig := baseDirectory.value / "project/scalastyle-config.xml"

scalafmtConfig := (baseDirectory in ThisBuild).value / "./project/.scalafmt.conf"

scalafmtOnCompile := true

startYear := Some(2019)

licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

headerLicense := {
  val toYear = java.time.Year.now
  Some(
    HeaderLicense.Custom(
      s"Copyright (C) 2018-$toYear  Cafienne B.V."
    )
  )
}

val akkaVersion      = "2.6.12"
val akkaHttpVersion  = "10.2.3"
val graalAkkaVersion = "0.5.0"
val boundedVersion   = "0.2.6-SNAPSHOT"
val slickVersion     = "3.3.3"
val swaggerVersion   = "2.1.6"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"                  % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson"  % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"                 % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-typed"           % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence"            % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed"      % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query"      % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"                % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools"          % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"                  % akkaVersion,
  "com.typesafe.akka" %% "akka-http"                   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"        % akkaHttpVersion,
  "ch.megard"                %% "akka-http-cors"        % "1.1.1",
  "com.lightbend.akka"       %% "akka-persistence-jdbc" % "5.0.0",
  "io.spray"                 %% "spray-json"            % "1.3.6",
  "net.virtual-void"         %% "json-lenses"           % "0.6.2",
  "org.iq80.leveldb"          % "leveldb"               % "0.12",
  "org.fusesource.leveldbjni" % "leveldbjni-all"        % "1.8",
  "io.cafienne.bounded"      %% "bounded-core"          % boundedVersion,
  "io.cafienne.bounded"      %% "bounded-test"          % boundedVersion % Test,
  "org.lmdbjava"              % "lmdbjava"              % "0.8.1", //used for storing the offset (at this moment)
  "com.typesafe.slick"       %% "slick"                 % slickVersion,
  //"org.slf4j" % "slf4j-nop" % "1.7.28", -> logback classic is included as dependency somewhere
  "com.typesafe.slick"         %% "slick-hikaricp"             % slickVersion,
  "org.hsqldb"                  % "hsqldb"                     % "2.5.1",
  "com.h2database"              % "h2"                         % "1.4.200",
  "org.postgresql"              % "postgresql"                 % "42.2.18",
  "com.nimbusds"                % "nimbus-jose-jwt"            % "9.4.2",
  "com.typesafe.scala-logging" %% "scala-logging"              % "3.9.2",
  //Don not update to 7, slick migration is not compatible
  "org.flywaydb"                % "flyway-core"                % "6.5.7",
  "io.github.nafg"             %% "slick-migration-api"        % "0.8.0",
  "io.github.nafg"             %% "slick-migration-api-flyway" % "0.7.0",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.0.1",
  "com.github.swagger-akka-http" %% "swagger-akka-http" % "2.4.0",
  "com.github.swagger-akka-http" %% "swagger-scala-module" % "2.3.0",
  "com.github.swagger-akka-http" %% "swagger-enumeratum-module" % "2.1.0",
  "io.swagger.core.v3" % "swagger-core" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-annotations" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-models" % swaggerVersion,
  "io.swagger.core.v3" % "swagger-jaxrs2" % swaggerVersion,
  "com.typesafe.akka"   %% "akka-actor-testkit-typed"  % akkaVersion     % Test,
  "com.typesafe.akka"   %% "akka-stream-testkit"       % akkaVersion     % Test,
  "com.typesafe.akka"   %% "akka-http-testkit"         % akkaHttpVersion % Test,
  "com.typesafe.akka"   %% "akka-persistence-testkit"  % akkaVersion     % Test,
  "org.scalatest"       %% "scalatest"                 % "3.2.3"         % Test,
  "net.virtual-void"    %% "json-lenses"               % "0.6.2"         % Test
)

//dependencyOverrides ++= Seq(
//  "com.fasterxml.jackson.core" % "jackson-databind"           % "2.10.5.1",
//  "com.fasterxml.jackson.core" % "jackson-annotations"        % "2.10.5"
//)

enablePlugins(GraalVMNativeImagePlugin)
enablePlugins(JavaAppPackaging, AshScriptPlugin)
enablePlugins(DockerPlugin)
enablePlugins(AutomateHeaderPlugin)
enablePlugins(AshScriptPlugin)
//enablePlugins(AppPlugin)
dockerExposedPorts := Seq(8086, 8087, 2552)

mainClass in Compile := Some("io.cafienne.bounded.cargosample.Boot")

dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre"

maintainer in Docker := """Cafienne <info@cafienne.io>"""

defaultLinuxInstallLocation in Docker := "/opt/cargo"

bashScriptExtraDefines in Docker += s"""addJava "-Dlogback.configurationFile=$${app_home}/../conf/logback.xml""""

bashScriptExtraDefines in Docker += s"""addJava "-Dconfig.file=$${app_home}/../conf/local.conf""""
