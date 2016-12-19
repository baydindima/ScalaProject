package example

import org.scalajs.dom
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.html.Form
import org.scalajs.dom.raw.Event
import org.scalajs.jquery.{jQuery => $}

import scala.scalajs.js

object MainJS extends js.JSApp {
  val Nick = "nickname"

  def main(): Unit = {

    val startChatForm = $("#start-chat-form").context.asInstanceOf[Form]
    val url = "http://" + dom.window.location.hostname + ":" + dom.window.location.port
    startChatForm.onsubmit = {
      e: Event =>
        e.preventDefault()
        val nick = $("#start-chat-text").`val`().asInstanceOf[String]
        val dataWsUrl = $("body").data("ws-url").asInstanceOf[String]
        val socket = new dom.WebSocket(dataWsUrl + s"?$Nick=$nick")
        var myAvatarId = "0"
        socket.onmessage = {
          message: MessageEvent =>
            val jsMessage = js.JSON.parse(message.data.toString)
            println(s"message.`type` = ${message.`type`}")
            println(s"message.data = ${message.data}")
            jsMessage.`type`.toString match {
              case "message" =>
                println("socket get message type message")
                $("#chatAndMessage")
                  .append("<div class=\"messageInChat\"><div class=\"messageClient\">"
                    + "<img align='left' src=\""
                    + url
                    + "/assets/images/" + jsMessage.img + ".png"
                    + "\" height=\"20\" width=\"20\" />" + jsMessage.nickname + ":" + jsMessage.msg + "</div></div>")
              case "info" =>
                println("socket get message type info")
                myAvatarId = jsMessage.img.toString
              case _ =>
            }
        }

        val sendMessageForm = $("#msgform").context.asInstanceOf[Form]
        sendMessageForm.onsubmit = {
          e: Event =>
            println("msg form handler")
            e.preventDefault()

            val json = js.JSON.stringify(js.Dynamic.literal(text = $("#msgtext").value()))
            println(json.toString)
            socket.send(json)

            $("#chatAndMessage").append("<div class=\"messageInChat\"><div class=\"messageManager\">"
              + "<img align='left' src=\"" + url
              + "/assets/images/" + myAvatarId + ".png" +
              "\" height=\"20\" width=\"20\" />" + $("#nickname").text() + ":" + $("#msgtext").`val`() + "</div></div>")
            $("#msgtext").`val`("")
        }


    }
  }
}
