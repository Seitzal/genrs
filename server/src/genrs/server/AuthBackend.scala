package genrs.server

import genrs._
import cats.effect.IO
import scala.collection.mutable
import org.http4s.Request
import org.http4s.headers.Authorization
import java.util.Base64

import cats.implicits._

trait AuthBackend {

  def registerUser(name: String, email: String, password: String): IO[User]

  def authenticate(name: String, password: String): IO[User]

  def authenticate(rq: Request[IO]): IO[User] = {
    val authHeader = rq
      .headers
      .get(Authorization)
      .map(_.renderString)
    authHeader match {
      case Some(s"Authorization: Basic $base64creds") => 
        IO {
          val decoder = Base64.getDecoder()
          val decoded = decoder.decode(base64creds.getBytes("UTF-8"))
          new String(decoded) match {
            case s"$name:$password" => authenticate(name, password)
            case _ => IO.raiseError(BadAuth)
          }
        }.flatten
      case Some(_) => IO.raiseError(BadAuth)
      case None => IO.raiseError(NoAuth)
    }
  }

  def getUser(id: Int): IO[User]
  def getUser(name: String): IO[User]

  // def getGroup(id: Int): IO[Group]
  // def getGroup(name: String): IO[Group]

  // def isMemberOf(userid: Int, groupid: Int): IO[Boolean]
  // def allGroups(userid: Int): IO[List[Group]]
  // def allUsers(groupid: Int): IO[List[User]]
}

object AuthBackend {

  /** An in-memory auth backend with no persistence mechanism. 
   *  Used for testing. */
  def ephemeral: AuthBackend = new AuthBackend {

    val users = new mutable.ArrayBuffer[(User, String)]

    def registerUser(name: String, email: String, password: String) = IO {
      val id = users.length
      val user = User(id, name, email)
      users.append((user, password))
      user
    }
    
    def authenticate(name: String, password: String) = IO {
      val user = users.find(_._1.name == name).getOrElse(throw UserNotFound)
      if (user._2 == password) user._1
      else throw WrongPassword
    }
    
    def getUser(id: Int) =
      IO(users(id)._1)
    
    def getUser(name: String) = 
      IO(users.find(_._1.name == name).map(_._1).get)

  }

}
