
package helpers

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue
import utils.Utils

case class SendNextGame(game: JsValue, request: Request, team_name: String)
case class SendPlayerStats(player: Seq[Any], request: Request)
case class SendHelp(request: Request)
case class SendError(request: Request, message: String)
case class SendInternalError(request: Request)
case class SendWelcome(request: Request)

abstract class Responder() extends Actor {

  val Utils = new Utils

  def receive = {
    case SendNextGame(game, request, team_name) => {
      this.respondNextGame(game, request, team_name)
    }
    case SendPlayerStats(stats, request) => {
      this.respondPlayerStats(stats, request)
    }
    case SendHelp(request) => {
      this.respondHelp(request)
    }
    case SendError(request, message) => {
      this.respondError(request, message)
    }
    case SendInternalError(request) => {
      this.respondInternalError(request)
    }
    case SendWelcome(request) => {
      this.respondWelcome(request)
    }
  }

  def respondNextGame(game: JsValue, request: Request , team_name: String)
  def respondPlayerStats(stats: Seq[Any], request: Request)
  def respondHelp(request: Request)
  def respondError(request: Request, message: String)
  def respondInternalError(request: Request)
  def respondWelcome(request: Request)
}
