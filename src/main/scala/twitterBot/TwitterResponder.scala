package twitterBot

import java.text.SimpleDateFormat
import java.time.{Instant, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.{Locale, Date}

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue


case class TweetNextGame(game: JsValue, tweet: Tweet, team_name: String)

class TwitterResponder() extends Actor {

  val client = TwitterRestClient()

  val inputDate = "2021/01/29 11:00";


  def timeZoneChangeToBsAs(date: String, status: String) = {
    val dateString = date.substring(0,10)
    var timeString = ""
    if(status(1) == ':') timeString = "0" + timeString.concat(status.substring(0,4))
    else timeString = timeString.concat(status.substring(0,5))
    val newDateString = dateString + " " + timeString + " PM"
    val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US)
    val localZone = LocalDateTime.parse(newDateString, sourceFormatter)
    val ESTzone = localZone.atZone(ZoneId.of("America/New_York"))
    val result = ESTzone.withZoneSameInstant(ZoneId.of("America/Buenos_Aires")).toString
    (result.substring(8,10) + "/" + result.substring(5,7) + "/" + result.substring(0,4), result.substring(11,16) + " hs")
  }

  def receive = {

      case TweetNextGame(game, tweet, team_name) => {
        val date_format = new SimpleDateFormat("dd/MM/yyyy")
        val local_team = game("home_team")("full_name").as[String].replaceAll("\\s", "")
        val visitor_team = game("visitor_team")("full_name").as[String].replaceAll("\\s", "")
        val (date, time) = timeZoneChangeToBsAs(game("date").as[String], game("status").as[String])
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} El próximo partido de los #${team_name} es:" +
            s"\n\n#$local_team vs #$visitor_team " +
            s"\nFecha: $date" +
            s"\nHorario: $time (Arg)" ,
          in_reply_to_status_id=Option(tweet.id)
        )
      }
  }
}