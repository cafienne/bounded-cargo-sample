import sbt._

object Dependencies {

  object Versions {
    val akka = "2.5.14"
    val akkaHttp = "10.1.4"
    val stamina = "0.1.4"
    val persistenceInMem = "2.5.1.1"
    val persistenceCassandra = "0.83"
    val bounded = "0.1.1"
    val guava = "20.0"
    val sprayJson = "1.3.4"
    val scalaLogging = "3.5.0"
    val scalaTest = "3.0.1"
  }

  val dependencies =
    Seq(
      "com.typesafe.akka"           %% s"akka-slf4j"                 % Versions.akka,
      "com.typesafe.akka"           %% s"akka-actor"                 % Versions.akka,
      "com.typesafe.akka"           %% s"akka-stream"                % Versions.akka,
      "com.typesafe.akka"           %% s"akka-persistence"           % Versions.akka,
      "com.typesafe.akka"           %% s"akka-persistence-query"     % Versions.akka,
      "com.typesafe.akka"           %% s"akka-stream-testkit"        % Versions.akka % Test,
      "com.typesafe.akka"           %% s"akka-testkit"               % Versions.akka % Test,
      "com.google.guava"            % "guava"                        % Versions.guava,
      "com.scalapenos"              %% "stamina-json"                % Versions.stamina,
      "io.spray"                    %% "spray-json"                  % Versions.sprayJson,
      "com.typesafe.akka"           %% "akka-persistence-cassandra"  % Versions.persistenceCassandra exclude("com.google.guava", "guava"), // force guava version 20 instead of 19
      "com.github.dnvriend"         %% "akka-persistence-inmemory"   % Versions.persistenceInMem,
      "com.typesafe.scala-logging"  %% "scala-logging"               % Versions.scalaLogging,
      "com.typesafe.akka"           %% s"akka-http"                  % Versions.akkaHttp,
      "com.typesafe.akka"           %% s"akka-http-spray-json"       % Versions.akkaHttp,
      "io.cafienne.bounded"         %% "bounded-core"                % Versions.bounded,
      "io.cafienne.bounded"         %% "bounded-akka-http"           % Versions.bounded,
      "io.cafienne.bounded"         %% "bounded-test"                % Versions.bounded % Test,
      "org.scalatest"               %% "scalatest"                   % Versions.scalaTest % Test,
      "com.scalapenos"              %% "stamina-testkit"             % "0.1.4" % Test,
      "org.scalamock"               %% "scalamock-scalatest-support" % "3.6.0" % Test,
      "com.danielasfregola"         %% "random-data-generator"       % "2.3" % Test,
      "com.typesafe.akka"           %% s"akka-http-testkit"          % Versions.akkaHttp % Test,
      "ch.qos.logback"              % "logback-classic"              % "1.2.3",
      "net.logstash.logback"        % "logstash-logback-encoder"     % "4.10",
      "io.swagger"                  % "swagger-jaxrs"                % "1.5.18",
      // As suggested in https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j
      // to resolve blow-up due to swagger :  java.lang.NoClassDefFoundError: javax/xml/bind/annotation/XmlRootElement.
      "javax.xml.bind"              % "jaxb-api"                     % "2.3.0",
      "com.github.swagger-akka-http" %% "swagger-akka-http"          % "0.14.0",
      "org.iq80.leveldb"            % "leveldb"                      % "0.9",
      "org.fusesource.leveldbjni"   % "leveldbjni-all"               % "1.8",
      "org.lmdbjava"                % "lmdbjava"                     % "0.6.0",
      "ch.lightshed"                %% "courier"                     % "0.1.4"
    )

}
