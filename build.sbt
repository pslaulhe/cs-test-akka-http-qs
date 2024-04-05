lazy val akkaHttpVersion = "10.6.0"
lazy val akkaVersion    = "2.9.0"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.12"
    )),
    name := "akka-http-quickstart-scala",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.megard" %% "akka-http-cors" % "1.2.0",
      "ch.qos.logback"    % "logback-classic"           % "1.5.2",

      "org.postgresql" % "postgresql" % "42.7.1",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.2.17"        % Test,
      "org.scalatestplus" %% "mockito-5-10"             % "3.2.18.0" % Test,
      "org.assertj"       % "assertj-core"             % "3.25.2" % Test

      // mockito for test doubles, jassert for fluent assertions?
    )
  )
