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
case class searchPlayerStats(player_firstname: String, player_lastname: String, tweet: Tweet)

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

  def searchPlayer(players_for_name: JsValue ,players_for_surname: JsValue) = {
    val total_players_for_name = (players_for_name("data")).as[List[JsObject]]
    val total_players_for_surname = (players_for_surname("data")).as[List[JsObject]]
    total_players_for_name.intersect(total_players_for_surname)
  }

  def playerStatsFinder(stats_data: JsValue, stats_meta: JsValue) = {
    val stats_lenght = stats_meta("total_count").as[Int] - 1
    val name_and_surname = stats_data(0)("player")("first_name").as[String] + stats_data(0)("player")("last_name").as[String]
    val position = stats_data(stats_lenght)("player")("position").as[String]
    val height_feet = stats_data(stats_lenght)("player")("height_feet").as[Int]
    val height_inches = stats_data(stats_lenght)("player")("height_inches").as[Int]
    val height = ((height_feet * 0.3048 + height_inches * 0.0254) * 100).round / 100.toDouble
    val weight = ((stats_data(stats_lenght)("player")("weight_pounds").as[Int] * 0.453592) * 100).round / 100.toDouble
    val team = stats_data(stats_lenght)("team")("full_name").as[String].replaceAll("\\s", "")
    var (pointsPerGame, reboundsPerGame, blocksPerGame, assistsPerGame, stealsPerGame) = (0.0,0.0,0.0,0.0,0.0)
    for (i <- 0 to stats_lenght) {
      pointsPerGame += stats_data(i)("pts").as[Int]
      reboundsPerGame += stats_data(i)("reb").as[Int]
      blocksPerGame += stats_data(i)("blk").as[Int]
      assistsPerGame += stats_data(i)("ast").as[Int]
      stealsPerGame += stats_data(i)("stl").as[Int]
    }

    var prev_stats = Seq(name_and_surname, team, position, height, weight)
    prev_stats ++ Seq(
      pointsPerGame,
      assistsPerGame,
      reboundsPerGame,
      blocksPerGame,
      stealsPerGame,
      ).map(stat => ((stat / stats_lenght) * 100).round / 100.toDouble)


  }

  def receive: Receive = {

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

    case searchPlayerStats(player_firstname, player_lastname, tweet) => {

      val futureNamePlayer = doRequest(s"players?per_page=100&search=$player_firstname")
      val futureSurnamePlayer = doRequest(s"players?per_page=100&search=$player_lastname")

      val player = for {
        player_for_name <- futureNamePlayer
        player_for_surname <- futureSurnamePlayer
      }yield(player_for_name, player_for_surname)

      player.onComplete{
        case Success((player_for_name,player_for_surname)) =>
          val player = searchPlayer(player_for_name, player_for_surname).head
          val player_id = player("id").as[Int]
          val futureStatsPlayer = doRequest(s"stats/?seasons[]=2020&per_page=110&player_ids[]=$player_id")

          futureStatsPlayer.onComplete{
            case Success(stats) =>
              TwitterResponder ! TweetPlayerStats(playerStatsFinder(stats("data"), stats("meta")), tweet)
            case Failure(e) => println(s"Hubo un error al buscar los stats $e")
          }
        case Failure(e) => println(s"Hubo un error con el player $e")
      }

    }
  }
}