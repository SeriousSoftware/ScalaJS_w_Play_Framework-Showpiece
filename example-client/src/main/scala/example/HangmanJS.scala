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
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

@JSExport
object HangmanJS {

  def pageGuess: TypedTag[dom.raw.HTMLElement] = div(`class` := "content") {
    div(
      div(h2("Please make a guess"),
        Rx {
          div(h3(style := "letter-spacing: 4px;")(Store.game().guessWord),
            p(Store.game().reportStatus),
            p("Guess:")(
              for (c <- 'A' to 'Z' diff Store.game().guess)
                yield {
                  a(c.toString)(style := "padding-left:10px;",
                    href := "javascript:void(0);",
                    onclick := { () => Store.guess(c) { () => if (Store.game().isEndOfGame) goto(pageResult) }
                    })
                }
            ))
        },
        br, a("Give up?", href := "javascript:void(0);",
          onclick := { () => Store.giveup { () => goto(pagePlay) } }
        )
      )
    )
  }

  def pagePlay: TypedTag[dom.raw.HTMLElement] = div {
    def levels = Seq(
      (10, "Easy"),
      (5, "Medium"),
      (3, "Hard"))

    div(
      p("Inspired from ")(a(href := "http://www.yiiframework.com/demos/hangman/", target := "_blank", "Yii's demo")),
      p("This is the game of Hangman. You must guess a word, a letter at a time.",
        "If you make too many mistakes, you lose the game!"),
      form(id := "playForm")(
        for ((level, text) <- levels) yield {
          val levelId = s"level_$level"
          div(`class` := "radio")(
            input(id := levelId, `type` := "radio", name := "level",
              onclick := { () => Store.level() = level },
              if (level == Store.level()) checked := "checked" else ()),
            label(`for` := levelId, style := "padding-left: 5px")(s"$text game; you are allowed $level misses.")
          )
        }, br,
        input(`type` := "button", value := "Play!", `class` := "btn btn-primary",
          onclick := {
            () => if (Store.level() > 0) {
              Store.`play!`()
              goto(pageGuess)
            } else dom.alert("Please select level!")
          })
      )
    )
  }

  def pageResult: TypedTag[dom.raw.HTMLElement] = div {
    div(
      h2(s"You ${if (Store.game().isWon) "Win" else "Lose"}!"),
      p(s"The word was: ${Store.game().word}"),
      p(Store.game().reportStatus), br,
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
  def main(): Unit =
    Store.session { resume =>
      dom.document.getElementById("content").appendChild(
        if (resume) if (Store.game().isEndOfGame) pageResult.render else pageGuess.render
        else pagePlay.render)
    }

  object Store {
    val (level, game) = (Var(0), Var(Hangman(0, "")))

    def `play!`(level: Int = Store.level()) = Ajax.postAsForm(Routes.Hangman.start(level)).map {
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
          Store.level() = 0
          callback()
        }
      }
  }

}