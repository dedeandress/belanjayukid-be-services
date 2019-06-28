name := """api-belanjayuk.id"""
organization := "BelanjaYuk.id"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

libraryDependencies ++= Seq(
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.postgresql" % "postgresql" % "42.2.5.jre7",
  "com.pauldijou" %% "jwt-play" % "2.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.pauldijou" %% "jwt-play-json" % "2.1.0"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "BelanjaYuk.id.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "BelanjaYuk.id.binders._"
