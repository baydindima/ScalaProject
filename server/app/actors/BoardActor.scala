package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.event.LoggingReceive
import play.libs.Akka

class BoardActor extends Actor with ActorLogging {
  var users = Set[ActorRef]()

  def receive = LoggingReceive {
    case m: Message =>
      println(s"Board get $m")
      users foreach { user =>
        user ! m
      }
    case Subscribe =>
      println(s"Board subscribe")
      users += sender
      context watch sender
    case Terminated(user) =>
      users -= user
  }
}

object BoardActor {
  lazy val board: ActorRef = Akka.system().actorOf(Props[BoardActor])
  def apply(): ActorRef = board
}

case class Message(nickname: String, userId: Int, msg: String)
object Subscribe
