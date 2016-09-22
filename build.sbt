import sbt.Keys._
import ReleaseTransformations._

organization := "ch.randm"
name := "units-of-information"

// Scala options
scalaVersion := "2.11.8"
scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

// Dependencies
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % Test

// Testing
scalacOptions in Test ++= Seq("-Yrangepos") // Used by Specs2 string parser

// ScalaDocs
autoAPIMappings := true

// Publishing
buildCredentials

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  //publishArtifacts, // Removed, since `git push` triggers build which will push the artifact to the repository
  setNextVersion,
  commitNextVersion,
  pushChanges
)

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
