import mill._, scalalib._

val globalScalaVersion = "2.13.1"
val upickle = "0.9.5"
val http4s = "0.21.2"
val doobie = "0.8.8"
val scalatest = "3.1.1"
val jwt = "4.2.0"

object testdata extends ScalaModule {
  def moduleDeps = Seq(core)
  def scalaVersion = globalScalaVersion
  def ivyDeps = Agg(
    ivy"com.lihaoyi::upickle:$upickle"
  )
}

object core extends ScalaModule {
  def scalaVersion = globalScalaVersion
  def ivyDeps = Agg(
    ivy"com.lihaoyi::upickle:$upickle",
    ivy"com.pauldijou::jwt-upickle:$jwt"
  )

  object test extends Tests {
    def moduleDeps = Seq(core, testdata)
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:$scalatest")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

object server extends ScalaModule {
  def moduleDeps = Seq(core)
  def scalaVersion = globalScalaVersion
  def ivyDeps = Agg(
    ivy"com.lihaoyi::upickle:$upickle",
    ivy"org.http4s::http4s-dsl:$http4s",
    ivy"org.http4s::http4s-blaze-server:$http4s",
    ivy"org.http4s::http4s-blaze-client:$http4s",
    ivy"org.tpolecat::doobie-core:$doobie",
    ivy"org.tpolecat::doobie-postgres:$doobie",
    ivy"com.pauldijou::jwt-upickle:$jwt",
    ivy"eu.seitzal::http4s-upickle:0.1.0"
  )

  object test extends Tests {
    def moduleDeps = Seq(core, server, testdata)
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:$scalatest")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}
