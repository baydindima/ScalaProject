package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import play.api.libs.json.{JsValue, Json}

import scala.xml.Utility

class UserActor(nick: String, userId: Int, board: ActorRef, out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = {
    println("pre start user actor")
    board ! Subscribe
    import UserActor._
    val js = Json.obj("type" -> "info", "img" -> userId % AvatarCount)
    out ! js
  }

  def receive = LoggingReceive {
    case Message(nickname, id, s) if sender == board =>
      import UserActor._
      if (nickname != nick) {
        val js = Json.obj("type" -> "message", "nickname" -> nickname, "msg" -> s, "img" -> id % AvatarCount)
        out ! js
      }
    case js: JsValue =>
      (js \ "msg").validate[String] map {
        Utility.escape
      } foreach {
        board ! Message(nick, userId, _)
      }
    case other =>
      log.error("unhandled: " + other)
  }
}

object UserActor {
  val AvatarCount = 11
  var userCount = 0

  def props(nick: String)(out: ActorRef) = {
    userCount += 1
    Props(new UserActor(nick, userCount - 1, BoardActor(), out))
  }
}
