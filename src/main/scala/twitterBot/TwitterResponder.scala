package twitterBot

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue


case class TweetNextGame(game: JsValue, tweet: Tweet, team_name: String)
case class TweetPlayerStats(player: Seq[Any], tweet: Tweet)
case class TweetHelp(tweet: Tweet)


class TwitterResponder() extends Actor {

  val Utils = new Utils
  val client = TwitterRestClient()

  def receive = {

      case TweetNextGame(game, tweet, team_name) => {
        val local_team = game("home_team")("full_name").as[String].replaceAll("\\s", "")
        val visitor_team = game("visitor_team")("full_name").as[String].replaceAll("\\s", "")
        val (date, time) = Utils.timeZoneChangeToBsAs(game("date").as[String], game("status").as[String])
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} El próximo partido de los #${team_name} es:" +
            s"\n\n#$local_team vs #$visitor_team " +
            s"\nFecha: $date" +
            s"\nHorario: $time (Arg)" ,
          in_reply_to_status_id=Option(tweet.id)
        )
      }

      case TweetPlayerStats(stats, tweet) => {
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} Datos del jugador #${stats(0)} :" +
          s"\n\nEquipo: #${stats(1)}" +
          s"\nPosicion: ${Utils.positionParse(stats(2).toString)}" +
          s"\nAltura: ${stats(3)} m" +
          s"\nPeso: ${stats(4)} kg" +
          s"\nPuntos por partido: ${stats(5)}" +
          s"\nAsistencias por partido: ${stats(6)}" +
          s"\nRebotes por partido: ${stats(7)}" +
          s"\nBloqueos por partido: ${stats(8)}" +
          s"\nRobos por partido: ${stats(9)}",
          in_reply_to_status_id=Option(tweet.id)
        )
      }

      case TweetHelp(tweet) => {
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} " +
            s"\n\nInstrucciones de uso: " +
            s"\n\n - Información del proximo partido de tu equipo: " +
            s"\n@NBAInformation6 #ProximoPartido #NombreDeTuEquipo" +
            s"\n\n- Información sobre tu jugador: " +
            s"\n@NBAInformation6 #Jugador #NombreDeTuJugador #ApellidoDeTuJugador",
          in_reply_to_status_id=Option(tweet.id)
        )
      }
  }
}