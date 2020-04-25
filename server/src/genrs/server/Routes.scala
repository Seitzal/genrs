package genrs.server

import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.headers._
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.headers.Authorization
import java.util.Base64
import java.time.Duration
import org.http4s.parser.ContentTypeHeader

class Routes(
    implicit auth: AuthBackend,
    secret: String,
    cs: ContextShift[IO], 
    timer: Timer[IO]) {

  def get = HttpRoutes.of[IO] {

    case rq @ POST -> Root / "auth" / "login" =>
      auth.authenticate(rq)
      .map(_.getJwt(Duration.ofHours(10)))
      .flatMap(Ok(_))
      .recoverWith {
        case NoAuth => IO(Response(status = Unauthorized))
        case BadAuth => BadRequest()
        case UserNotFound => IO(Response(status = Unauthorized))
        case WrongPassword => IO(Response(status = Unauthorized))
      }
 
  }.orNotFound

}