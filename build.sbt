import sbt.Keys._

lazy val root = (project in file(".")).
  settings(
    name := "units-of-information",
    version := "0.1",
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