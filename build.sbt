lazy val root = (project in file(".")).
  settings(
    name := "units-of-information",
    version := "1.0",
    scalaVersion := "2.11.8",
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-a"),
    libraryDependencies ++= Seq(
        "junit" % "junit" % "4.12" % Test,
        "com.novocode" % "junit-interface" % "0.11" % Test
          exclude("junit", "junit-dep")
    )
  )