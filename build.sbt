lazy val root = (project in file(".")).
  settings(
    name := "units-of-information",
    version := "0.1",
    scalaVersion := "2.11.8",
    scalacOptions in Test ++= Seq("-Yrangepos"),
    libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % Test
  )