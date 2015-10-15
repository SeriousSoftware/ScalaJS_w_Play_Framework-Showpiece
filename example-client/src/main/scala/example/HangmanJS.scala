package example

import common.ExtAjax._
import common.Framework._
import config.Routes
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import rx._
import shared.Hangman
import upickle.default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom._
import scalatags.JsDom.all._

@JSExport
object HangmanJS {

  def pageGuess: TypedTag[dom.raw.HTMLElement] = div(`class` := "content") {
    div(
      Rx {
        div(
          h2("Please make a guess"),
          h3(style := "letter-spacing: 4px;")(Model.game().guessWord().mkString),
          p(s"You have made ${Model.game().misses} bad guesses out of a maximum of ${Model.game().level}"),
          p("Guess:")(
            for (c <- 'A' until 'Z'
                 if !Model.game().guess.contains(c))
              yield {
                a(c.toString)(style := "padding-left:10px;",
                  href := "javascript:void(0);",
                  onclick := { () => Model.guess(c) {
                    () => if (Model.game().gameOver()) goto(pageResult)
                  }
                  })
              }
          ), br,
          a("Give up?", href := "javascript:void(0);", onclick := { () =>
            Model.giveup { () => goto(pagePlay) }
          })
        )
      }
    )
  }

  def pagePlay: TypedTag[dom.raw.HTMLElement] = div {
    val levels = Seq(
      (10, "Easy game; you are allowed 10 misses."),
      (5, "Medium game; you are allowed 5 misses."),
      (3, "Hard game; you are allowed 3 misses.")
    )
    div(
      p("Inspired from ")(a(href := "http://www.yiiframework.com/demos/hangman/", target := "_blank", "Yii's demo")),
      p("This is the game of Hangman. You must guess a word, a letter at a time.",
        "If you make too many mistakes, you lose the game!"),
      form(id := "playForm")(
        for ((level, text) <- levels) yield {
          val levelId = s"level_$level"
          div(`class` := "radio")(
            input(id := levelId,
              `type` := "radio",
              name := "level",
              onclick := { () => Model.level() = level },
              if (level == Model.level()) checked := "checked" else {}),
            label(`for` := levelId, style := "padding-left: 5px")(text)
          )
        }, br,
        input(`type` := "button", value := "Play!", `class` := "btn btn-primary", onclick := {
          () => if (Model.level() > 0) {
            Model.start()
            goto(pageGuess)
          } else dom.alert("Please select level!")
        })
      )
    )
  }

  def pageResult: TypedTag[dom.raw.HTMLElement] = div {
    div(
      h2(if (Model.game().won()) "You Win!" else "You Lose!"),
      p(s"The word was: ${Model.game().word}"), br,
      input(`type` := "button",
        value := "Start Again!",
        `class` := "btn btn-primary",
        onclick := { () => goto(pagePlay) })
    )
  }

  def goto(page: TypedTag[dom.raw.HTMLElement]) = {
    val lastChild = dom.document.getElementById("content").lastChild
    dom.document.getElementById("content")
      .replaceChild(div(style := "margin-left:20px;")(page).render, lastChild)
  }

  @JSExport
  def main(): Unit = {
    Model.session { resume =>
      dom.document.getElementById("content").appendChild(
        if (resume) if (Model.game().gameOver()) pageResult.render else pageGuess.render
        else pagePlay.render)
    }
  }

  object Model {
    val (level, game) = (Var(0), Var(Hangman(0, "")))

    def start(level: Int = Model.level()) = Ajax.postAsForm(Routes.Hangman.start(level)).map {
      r => game() = read[Hangman](r.responseText)
    }

    def session(callback: (Boolean) => Unit) =
      Ajax.get(Routes.Hangman.session).map { r =>
        game() = read[Hangman](r.responseText)
        level() = game().level
        callback(true)
      }.recover { case e => callback(false) }

    def guess(g: Char)(callback: () => Unit) =
      Ajax.postAsForm(Routes.Hangman.guess(g)).map { r =>
        if (r.ok) {
          game() = read[Hangman](r.responseText)
          callback()
        }
      }

    def giveup(callback: () => Unit) =
      Ajax.postAsForm(Routes.Hangman.giveup).map { r =>
        if (r.ok) {
          Model.level() = 0
          callback()
        }
      }
  }

}