package twitterBot

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue


case class TweetNextGame(game: JsValue, tweet: Tweet, team_name: String)
case class TweetPlayerStats(player: JsValue, tweet: Tweet)


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

      case TweetPlayerStats(player, tweet) => {
        val name_and_surname = player("first_name").as[String] + player("last_name").as[String]
        val position = player("position").as[String]
        val team = player("team")("full_name").as[String].replaceAll("\\s", "")
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} Datos del jugador #${name_and_surname} :" +
            s"\n\nPosicion: #$position" +
            s"\nEquipo: #$team",
          in_reply_to_status_id=Option(tweet.id)
        )
      }
  }
}