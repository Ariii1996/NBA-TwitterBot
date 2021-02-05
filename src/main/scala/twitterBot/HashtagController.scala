package twitterBot

import akka.actor.{Actor, ActorRef, Status}
import com.danielasfregola.twitter4s.entities.Tweet
import play.api.libs.json.JsValue

case class ProcessRequest(request: Request, responder: ActorRef)
case class ResolvesNextGame(game: JsValue, request: Request, team_name: String)
case class ResolvesPlayerStats(player: Seq[Any], request: Request)
case class ResolvesError(request: Request, message: String)

class HashtagController(NBArequester: ActorRef) extends Actor {

  var request: Request = null
  var Responder: ActorRef = null

  def receive: Receive = {

    case ProcessRequest(request, responder) => {

      this.request = request
      this.Responder = responder

      var (action, option1, option2) = request.getAction()

      action match {
        case "Jugador" => {
          if(option1 != "" & option2 != "")
            NBArequester ! searchPlayerStats(option1, option2, request)
          else Responder ! ResolvesError(request, "No se introdujo el nombre o el apellido del jugador")
        }
        case "Proximopartido" => {
          if(option1 != "") NBArequester ! searchTeamNextGame(option1, request)
          else Responder ! SendError(request, "No se introdujo el nombre del equipo")
        }
        case "Ayuda" => Responder ! SendHelp(request)
        case "Welcome" => Responder ! SendWelcome(request)
        case _ => Responder ! SendError(request, "No se introdujo ninguna acción")
      }
    }
    case ResolvesNextGame(game, request, team_name) => {
      Responder ! SendNextGame(game, request, team_name)
    }
    case ResolvesPlayerStats(stats, request) => {
      Responder ! SendPlayerStats(stats, request)
    }
    case SendError(request, message) => {
      Responder ! SendError(request, message)
    }
    case Status.Failure(error) => {
      println(s"Hubo un error al hacer la request $error")
      Responder ! SendInternalError(request)
    }
  }
}
