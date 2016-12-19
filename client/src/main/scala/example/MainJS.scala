package example

import org.scalajs.dom
import org.scalajs.dom.MessageEvent
import org.scalajs.dom.html.Form
import org.scalajs.dom.raw.Event
import org.scalajs.jquery.{JQuery, jQuery => $}

import scala.scalajs.js

object MainJS extends js.JSApp {
  val Nick = "nickname"

  def setInvisible(elem: JQuery): Unit = {
    elem.hide()
  }

  def setVisible(elem: JQuery): Unit = {
    elem.show()
  }

  def getImageUrl(imgName: String): String =
    s"/assets/images/$imgName.png"

  def getImageHtml(srcUrl: String, imgUrl: String): String =
    "<img align='left' src=\"" +
      srcUrl +
      getImageUrl(imgUrl) +
      "\" height=\"20\" width=\"20\" />"


  def main(): Unit = {

    val $startChatForm = $("#start-chat-form")
    val $chatAndMessage = $("#chatAndMessage")

    setInvisible($chatAndMessage.parent())

    val startChatForm = $startChatForm.context.asInstanceOf[Form]
    val url = "http://" + dom.window.location.hostname + ":" + dom.window.location.port
    startChatForm.onsubmit = {
      e: Event =>
        e.preventDefault()
        val $startChatText = $("#start-chat-text")
        val nick = $startChatText.`val`().asInstanceOf[String]
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
                $chatAndMessage
                  .append("<div class=\"messageInChat\"><div class=\"messageClient\">"
                    + getImageHtml(url, jsMessage.img.toString) + jsMessage.nickname + ":" + jsMessage.msg + "</div></div>")
              case "info" =>
                println("socket get message type info")
                myAvatarId = jsMessage.img.toString
              case _ =>
            }
        }

        setInvisible($startChatForm)
        setVisible($chatAndMessage.parent())

        val $msgForm = $("#msgform")
        val sendMessageForm = $msgForm.context.asInstanceOf[Form]
        sendMessageForm.onsubmit = {
          e: Event =>
            println("msg form handler")
            e.preventDefault()

            val json = js.JSON.stringify(js.Dynamic.literal(text = $("#msgtext").value()))
            println(json.toString)
            socket.send(json)

            $chatAndMessage.append("<div class=\"messageInChat\"><div class=\"messageManager\">"
              + getImageHtml(url, myAvatarId) + nick + ":" + $("#msgtext").`val`() + "</div></div>")
            $("#msgtext").`val`("")
        }


    }
  }
}
