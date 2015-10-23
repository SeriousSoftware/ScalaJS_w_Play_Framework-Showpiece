package controllers

import play.api.mvc.{Action, Controller, RequestHeader}
import shared.Hangman
import upickle.default.{read, write}

import scala.io.Source
import scala.util.Random

object HangmanController extends Controller {

  lazy val (rand, separateWords) = (new Random(),
    Source.fromInputStream(getClass.getResourceAsStream("/public/text/words.txt")).
      mkString.
      split( """[\s,]+""").
      filter(word => word.length > 5 && word.forall(Character.isLetter)).
      map(_.toUpperCase).distinct)
  // Deduplicate plural form if singular form exist
  lazy val words = separateWords diff separateWords.map(_ + "S")

  def index = Action {
    implicit request => Ok(views.html.hangman(readSession))
  }

  private def readSession(implicit request: RequestHeader): Option[Hangman] =
    request.session.get(sessionName).map { hangman => read[Hangman](hangman) }

  def start(level: Int) = Action { implicit request =>
    val value = write(Hangman(level, words(rand.nextInt(words.length))))
    Ok(value).withSession(writeSession(value))
  }

  private def writeSession(value: String)(implicit request: RequestHeader) =
    request.session + (sessionName -> value)

  def sessionName = "hangman"

  def session = Action { implicit request =>
    readSession.map { o => Ok(write(o)) }.getOrElse(NotFound)
  }

  def guess(g: Char) = Action { implicit request =>
    readSession.map { hangman =>
      val value = write(hangman.copy(`guess` = hangman.guess :+ g,
        misses = if (hangman.word.contains(g)) hangman.misses else hangman.misses + 1))
      Ok(value).withSession(writeSession(value))
    }.getOrElse(BadRequest)
  }

  def giveup = Action { implicit request =>
    (if (isAjax) Ok else Redirect(routes.HangmanController.index()))
      .withSession(request.session - sessionName)
  }

}