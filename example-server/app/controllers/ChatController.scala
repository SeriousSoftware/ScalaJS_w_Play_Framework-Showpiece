package controllers

import models.ChatRoom
import play.api.data.Forms._
import play.api.data._
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, Controller, WebSocket}

object ChatController extends Controller {

  val talkForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "msg" -> text
    )
  )

  def index = Action { implicit request =>
    Ok(views.html.chat())
  }

  /**
   * Handles the chat websocket.
   */
  def chatWS(username: String) = WebSocket.tryAccept[JsValue] { request  =>

    ChatRoom.join(username).map{ io =>
      Right(io)
    }.recover{ case e => Left(Ok(e))}
  }

  def chatSSE(username: String) = Action.async { request =>
    ChatRoom.join(username).map { io =>
      Ok.feed(io._2
        &> Concurrent.buffer(50)
        &> Enumeratee.onIterateeDone { () =>
        play.Logger.info(request.remoteAddress + " - SSE disconnected")
      }
        &> EventSource()).as("text/event-stream")
    }.recover { case e => BadRequest(e) }
  }

  def talk = Action { implicit request =>
    talkForm.bindFromRequest.fold(
      error => BadRequest,
      value => {
        ChatRoom.talk(value._1, value._2)
        Ok
      }
    )
  }

}
