import sbt.Keys._
import ReleaseTransformations._

name            := "Units Of Information"
normalizedName  := "units-of-information"
description     := "An immutable Scala class to represent units of information (file size, disk space, memory)"
homepage        := Some(new URL("https://git.randm.ch/randm/units-of-information"))
startYear       := Some(2016)
organization    := "ch.randm"
autoAPIMappings := true

// Scala options
scalaVersion  := "2.12.0"
scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

// Dependencies
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.6" % Test

// Testing
scalacOptions in Test ++= Seq("-Yrangepos") // Used by Specs2 string parser

// Releasing
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

// Publishing
buildCredentials

publishTo := {
  val nexus = "https://repo.randm.ch/repository/maven-"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "snapshots")
  else
    Some("releases"  at nexus + "releases")
}

lazy val buildCredentials: sbt.SettingsDefinition = {
  (for {
    username <- Option(System.getenv().get("DEPLOY_USERNAME"))
    password <- Option(System.getenv().get("DEPLOY_PASSWORD"))
  } yield {
    credentials += Credentials("Sonatype Nexus Repository Manager", "repo.randm.ch", username, password)
  }).getOrElse(credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"))
}
