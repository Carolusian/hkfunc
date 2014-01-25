name := "play-monitor"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

unmanagedBase := baseDirectory.value / "custom_lib"

play.Project.playScalaSettings
