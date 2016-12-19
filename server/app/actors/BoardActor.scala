package actors

import java.io.{File, FileWriter}

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.event.LoggingReceive
import play.libs.Akka

import scala.io.Source
import scala.util.matching.Regex

class BoardActor extends Actor with ActorLogging {
  var users = Set[ActorRef]()
  val file = new File("history.log")

  def appendToFile(f: File)(op: FileWriter => Unit) {
    val p = new FileWriter(f, true)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  def receive = LoggingReceive {
    case m: Message =>
      println(s"Board get $m")

      appendToFile(file) { p =>
        p.write(m.toString + "\n")
      }

      users foreach { user =>
        user ! m
      }
    case Subscribe =>
      println(s"Board subscribe")

      Source.fromFile(file).getLines().foreach {
        case MessageString(message) =>
          sender ! message
        case line =>
          log.error(s"Invalid message line: $line")
      }

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

object AsInt {
  def unapply(s: String): Option[Int] = try {
    Some(s.toInt)
  } catch {
    case e: NumberFormatException => None
  }
}


case class Message(nickname: String, userId: Int, msg: String)


object MessageString {
  val MessageRegex: Regex = """Message\((.*),(\d+),(.*)\)""".r

  def unapply(str: String): Option[Message] = str match {
    case MessageRegex(nick, AsInt(userId), msg) => Some(Message(nick, userId, msg))
    case _ => None
  }
}

object Subscribe
