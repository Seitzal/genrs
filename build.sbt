scalaVersion := "0.23.0-RC1"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "cask" % "0.5.2",
  "com.lihaoyi" %% "upickle" % "0.9.5",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test"
).map(_.withDottyCompat(scalaVersion.value))

Compile / scalaSource := baseDirectory.value / "src" / "app"
Test    / scalaSource := baseDirectory.value / "src" / "test"
