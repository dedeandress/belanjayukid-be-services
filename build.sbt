name := """belanjayukid-be-api"""
organization := "BelanjaYuk.id"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Atlassian Maven Repository" at "https://maven.atlassian.com/content/repositories/atlassian-public/"


scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

libraryDependencies ++= Seq(
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.postgresql" % "postgresql" % "42.2.5.jre7",
  "com.pauldijou" %% "jwt-play" % "2.1.0",
  "com.pauldijou" %% "jwt-play-json" % "2.1.0"
)

libraryDependencies ++= Seq(
  ehcache,
  ws,
  evolutions,
  guice,
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.5",
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.1",
  "com.h2database" % "h2" % "1.4.197",
  "com.auth0" % "java-jwt" % "3.3.0",
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.typesafe.slick" %% "slick" % "3.3.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
  "io.spray" %%  "spray-json" % "1.3.5",
  "com.sendgrid" % "sendgrid-java" % "4.4.1",
  "com.sendgrid" % "java-http-client" % "4.2.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "BelanjaYuk.id.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "BelanjaYuk.id.binders._"
