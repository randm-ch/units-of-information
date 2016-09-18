import sbt.Keys._

lazy val root = (project in file(".")).
  settings(
    organization := "ch.randm",
    name := "units-of-information",
    scalaVersion := "2.11.8",
    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true,
    exportJars := true,
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % Test,
    publishTo := {
      val nexus = "https://repo.randm.ch/repository/maven-"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "snapshots")
      else
        Some("releases"  at nexus + "releases")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )