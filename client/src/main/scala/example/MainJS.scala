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
        var myAvatarId = "0"
        socket.onmessage = {
          message: MessageEvent =>
            val jsMessage = js.JSON.parse(message.data.toString)
            message.`type` match {
              case "message" =>
                $("#chatAndMessage")
                  .append("<div class=\"messageInChat\"><div class=\"messageClient\">"
                    + "<img align='left' src=\""
                    + "http://" + dom.window.location.hostname + ":" + dom.window.location.port
                    + "/assets/images/" + jsMessage.img + ".png"
                    + "\" height=\"20\" width=\"20\" />" + jsMessage.nickname + ":" + jsMessage.msg + "</div></div>")
              case "info" =>
                myAvatarId = jsMessage.img.toString
              case _ =>
                println(s"message.`type` = ${message.`type`}")
                println(s"message.data = ${message.data}")
            }
        }
    }
  }
}
