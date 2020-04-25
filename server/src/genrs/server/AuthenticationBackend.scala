package genrs.server

import genrs._
import cats.effect.IO

trait AuthenticationBackend {

  def authenticate(name: String, password: String): IO[Option[User]]

  def getUser(id: Int): IO[Option[User]]
  def getUser(name: String): IO[Option[User]]

  def getGroup(id: Int): IO[Option[Group]]
  def getGroup(name: String): IO[Option[Group]]

  def isMemberOf(userid: Int, groupid: Int): IO[Boolean]
  def allGroups(userid: Int): IO[List[Group]]
  def allUsers(groupid: Int): IO[List[User]]

}