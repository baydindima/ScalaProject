package controllers

import actors.UserActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject._

import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.Future


class Application @Inject() (implicit system: ActorSystem, materializer: Materializer)  extends Controller{

  val Nick = "nickname"
  val Address = "address"
  val Msg = "msg"

  def index = Action {
    Ok(views.html.index("it works")).withNewSession
  }

  def chat = Action { implicit request =>
    Ok(views.html.chat(request.queryString(Nick).head))
      .withSession(request.session + (Nick -> request.queryString(Nick).head))
  }

  def ws = WebSocket.acceptOrResult[JsValue, JsValue] { implicit request =>
    Future.successful(request.session.get(Nick) match {
      case None => Left(Forbidden)
      case Some(nick) => Right(ActorFlow.actorRef(UserActor.props(nick)))
    })
  }

}
