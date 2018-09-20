import sbt._

object Dependencies {

  val akkaVersion = "2.5.14"
  val staminaVersion = "0.1.4"
  val persistenceInMemVersion = "2.5.1.1"
  val boundedVersion = "0.1.4"

  val baseDeps = {
    def akkaModule(name: String, version: String = akkaVersion) =
      "com.typesafe.akka" %% s"akka-$name" % version
    def akkaHttpModule(name: String, version: String = "10.1.3") =
      "com.typesafe.akka" %% s"akka-$name" % version
    Seq(
      akkaModule("slf4j"),
      akkaModule("actor"),
      akkaModule("stream"),
      akkaModule("persistence"),
      akkaModule("persistence-query"),
      akkaModule("stream-testkit") % Test,
      akkaModule("testkit") % Test,
      "com.google.guava"            % "guava"                                   % "20.0",
      "com.scalapenos"              %% "stamina-json"                           % staminaVersion,
      "io.spray"                    %% "spray-json"                             % "1.3.4",
      "com.typesafe.akka"           %% "akka-persistence-cassandra"             % "0.83" exclude("com.google.guava", "guava"), // force guava version 20 instead of 19
      "com.github.dnvriend"         %% "akka-persistence-inmemory"              % persistenceInMemVersion,
      "com.typesafe.scala-logging"  %% "scala-logging"                          % "3.5.0",
      akkaHttpModule("http"),
      akkaHttpModule("http-spray-json"),
      "io.cafienne.bounded" %% "bounded-core"                % boundedVersion,
      "io.cafienne.bounded" %% "bounded-akka-http"           % boundedVersion,
      "io.cafienne.bounded" %% "bounded-test"                % boundedVersion % Test,
      "org.scalatest"       %% "scalatest"                   % "3.0.1" % Test,
      "com.scalapenos"      %% "stamina-testkit"             % "0.1.4" % Test,
      "org.scalamock"       %% "scalamock-scalatest-support" % "3.6.0" % Test,
      "com.danielasfregola" %% "random-data-generator"       % "2.3" % Test,
      akkaHttpModule("http-testkit") % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.10",
      "io.swagger" % "swagger-jaxrs" % "1.5.18",
      // As suggested in https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j
      // to resolve blow-up due to swagger :  java.lang.NoClassDefFoundError: javax/xml/bind/annotation/XmlRootElement.
      "javax.xml.bind" % "jaxb-api" % "2.3.0",
      "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.14.0"

    ) 
  }

  val persistenceLevelDBDeps = {
    baseDeps ++ Seq(
      "org.iq80.leveldb"            % "leveldb"        % "0.9",
      "org.fusesource.leveldbjni"   % "leveldbjni-all" % "1.8"
    )
  }

  val persistanceLmdbDBDeps = {
    baseDeps ++ Seq(
      "org.lmdbjava"                % "lmdbjava"        % "0.6.0"
    )
  }


  val notifier = Seq(
    "ch.lightshed" %% "courier" % "0.1.4"
  )
}
