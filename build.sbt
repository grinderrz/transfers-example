lazy val root = (project in file(".")).
  settings(
    name := "transfers",
    scalaVersion := "2.12.2",
    version := "0.1",
    mainClass in Compile := Some("transfers.Boot")
  )

libraryDependencies ++= Seq(
  "org.scala-stm" %% "scala-stm" % "0.8",
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % "test"
)

scalacOptions += "-deprecation"
scalacOptions += "-feature"
