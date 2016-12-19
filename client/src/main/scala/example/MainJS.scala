package example

import org.scalajs.dom
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.jquery.{jQuery => $}

import scala.scalajs.js

object MainJS extends js.JSApp {
  val Nick = "nickname"

  def main(): Unit = {

    val button = dom.document.getElementById("start-chat-button").asInstanceOf[Button]
    button.onclick = {
      _: MouseEvent =>
        val nick = $("#start-chat-text").`val`().asInstanceOf[String]
        val dataWsUrl = $("body").data("ws-url").asInstanceOf[String]
        val socket = new dom.WebSocket(dataWsUrl + s"?$Nick=$nick")

        socket.onmessage = {
          message: MessageEvent =>
            println(s"message.`type` = ${message.`type`}")
            println(s"message.data = ${message.data}")

        }
    }


  }
}
