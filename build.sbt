scalaVersion := "0.22.0-RC1"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "cask" % "0.5.2",
  "com.lihaoyi" %% "upickle" % "0.9.5",
).map(_.withDottyCompat(scalaVersion.value))

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "com.typesafe" % "config" % "1.4.0"
)

Compile / scalaSource := baseDirectory.value / "src" / "app"
Test    / scalaSource := baseDirectory.value / "src" / "test"
