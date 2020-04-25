package genrs.server

import cats.effect._
import cats.implicits._

import org.http4s.server.blaze._

object Server extends IOApp {

  // BEGIN TEST CODE

  implicit val auth = AuthBackend.ephemeral

  auth.registerUser("alex", "alex@seitzal.eu", "nopassword").unsafeRunSync

  implicit val secret = "foo"

  // END TEST CODE

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp((new Routes).get)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
