
name := "ScalaProject"

lazy val scalaV = "2.11.8"

logLevel := Level.Debug

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  mainClass := Some("controllers.Application"),
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,
  libraryDependencies ++= Seq(
    ws, // Play's web services module
    "com.vmunier" %% "scalajs-scripts" % "1.0.0"
  )
).enablePlugins(PlayScala)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "be.doeraene" %%% "scalajs-jquery" % "0.8.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb)

onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value