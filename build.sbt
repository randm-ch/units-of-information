import sbt.Keys._

organization := "ch.randm"
name := "units-of-information"

scalaVersion := "2.11.8"
scalacOptions in Test ++= Seq("-Yrangepos")

autoAPIMappings := true
exportJars := true

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % Test

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