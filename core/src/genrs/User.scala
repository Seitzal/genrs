package genrs

import upickle.default._
import java.time.{Instant, Duration}
import pdi.jwt._
import scala.util.Try

final case class User(id: Int, name: String, email: String) {

  def getJwt(duration: Duration)(implicit secret: String): String =
    JwtUpickle.encode(
      JwtClaim(
        content = write(this),
        issuedAt = Some(Instant.now.getEpochSecond),
        expiration = Some(Instant.now.plus(duration).getEpochSecond),
        subject = Some(this.id.toString)
      ),
      secret, JwtAlgorithm.HS256
    )

}

object User {

  implicit val rw: ReadWriter[User] = macroRW

  def fromJwt(jwt: String)(implicit secret: String): Try[User] =
    JwtUpickle
      .decode(jwt, secret, Seq(JwtAlgorithm.HS256))
      .map(claim => read[User](claim.content))

  def fromJwtUnchecked(jwt: String): Try[User] =
    JwtUpickle
      .decode(jwt)
      .map(claim => read[User](claim.content))

}
