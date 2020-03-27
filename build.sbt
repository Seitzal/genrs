scalaVersion := "0.22.0-RC1"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "0.9.5",
).map(_.withDottyCompat(scalaVersion.value))

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.1.1",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test"
)

Compile / scalaSource := baseDirectory.value / "src" / "app"
Test    / scalaSource := baseDirectory.value / "src" / "test"
