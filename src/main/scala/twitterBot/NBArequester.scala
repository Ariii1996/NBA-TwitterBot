package twitterBot

import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.danielasfregola.twitter4s.entities.Tweet

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import play.api.libs.json._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration.DurationInt

case class searchTeamNextGame(team_name: String, tweet: Tweet)

class NBArequester(system: ActorSystem, TwitterResponder: ActorRef) extends Actor {


  def doRequest(query: String) = {
    implicit val actorSystem = system
    implicit val timeout: Timeout = 5.seconds

    Http().singleRequest(HttpRequest(uri = s"https://www.balldontlie.io/api/v1/$query"))
      .flatMap { res =>
        Unmarshal(res).to[String].map { data =>
          Json.parse(data)
        }
      }
  }

  def searchNextGame(games: JsValue) = {
    val games_list = games("data").as[JsArray]
    var next_game_id = (games("data")(0) \ "id").get.as[Long]
    var game_position = 0
    for (i <- 2 to games_list.value.size) {
      val game_id = (games_list(i-1) \ "id").get.as[Long]
      if(next_game_id > game_id){
        next_game_id = game_id
        game_position = i-1
      }
    }
    games_list(game_position)
  }

  def searchTeamId(teams: JsValue ,team: String): Int = {
    var id = -1
    for (i <- 1 to 30) {
      val team_name = (teams("data")(i - 1) \ "name").get.as[String]
      if (team_name == team) id = i
    }
    id
  }

  def receive = {

    case searchTeamNextGame(team_name, tweet) => {

      val responseFuture = doRequest("teams")

      responseFuture.onComplete {
        case Success(teams) =>
          val id_team = searchTeamId(teams, team_name)
          if (id_team > 0){
            val format = new SimpleDateFormat("yyyy-MM-dd")
            val today = format.format(Calendar.getInstance().getTime())
            val responseFuture = doRequest(s"games?team_ids[]=${id_team.toString}&start_date=$today")

            responseFuture.onComplete {
              case Success(games) =>
                val game = searchNextGame(games)
                TwitterResponder ! TweetNextGame(game, tweet, team_name)
              case Failure(e) =>
                println(s"Error buscando id de equipo: $e")
            }
          }else println("El nombre del equipo es incorrecto")
        case Failure(e) =>
          println(s"Error buscando equipos: $e")
      }
    }
  }
}
