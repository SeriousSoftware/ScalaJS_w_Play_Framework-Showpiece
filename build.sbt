lazy val clients = Seq(exampleClient)
val scalaV = "2.11.7"

lazy val exampleServer = (project in file("example-server")).settings(
  libraryDependencies ++= Seq(
    evolutions,
    filters,
    jdbc,
    specs2 % Test,
    "com.lihaoyi" %% "upickle" % "0.3.6",
    "com.typesafe.play" %% "anorm" % "2.5.0",
    "com.typesafe.slick" %% "slick" % "3.1.0",
    "com.vmunier" %% "play-scalajs-scripts" % "0.3.0",
    "org.webjars" % "bootstrap" % "3.3.5" //,
//  "org.webjars" % "font-awesome" % "4.4.0",
//  "org.webjars" % "jquery" % "2.1.4",
//  "org.webjars" %% "webjars-play" % "2.4.0"
  ),
  pipelineStages := Seq(scalaJSProd, gzip),
  routesImport += "config.Routes._",
  scalaJSProjects := clients,
  scalacOptions in Test ++= Seq("-Yrangepos", "-feature"),
  scalaVersion := scalaV
).enablePlugins(PlayScala).
  aggregate(clients.map(sbt.Project.projectToRef): _*).
  dependsOn(exampleSharedJvm)

lazy val exampleClient = (project in file("example-client")).settings(
  jsDependencies += RuntimeDOM % "test",
  libraryDependencies ++= Seq(
    "be.doeraene" %%% "scalajs-jquery" % "0.8.1",
    "com.lihaoyi" %%% "scalarx" % "0.2.8",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.lihaoyi" %%% "upickle" % "0.3.6",
    "com.lihaoyi" %%% "utest" % "0.3.1",
    "org.scala-js" %%% "scalajs-dom" % "0.8.2"),
  persistLauncher := true,
  persistLauncher in Test := false,
  scalaVersion := scalaV,
  sourceMapsDirectories += exampleSharedJs.base / "..",
  testFrameworks += new TestFramework("utest.runner.Framework")
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(exampleSharedJs)

lazy val exampleShared = (crossProject.crossType(CrossType.Pure) in file("example-shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val exampleSharedJvm = exampleShared.jvm
lazy val exampleSharedJs = exampleShared.js

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project exampleServer", _: State)) compose (onLoad in Global).value

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false