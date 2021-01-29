package twitterBot

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue


case class TweetNextGame(game: JsValue, tweet: Tweet, team_name: String)

class TwitterResponder() extends Actor {

  val client = TwitterRestClient()

  var next_day = false

  def argentinaTime(etTime: String): String ={
    next_day = false
    var new_time = ""
    if(etTime(1) == ':'){
      val arg_time = etTime.substring(0,1).toInt + 14
      new_time = s"${arg_time.toString}${etTime.substring(1,5)} hs"
    }else{
      var arg_time = etTime.substring(0,2).toInt + 14
      if (arg_time >= 24) {
        arg_time -= 24
        next_day = true
      }
      new_time = s"${arg_time.toString}${etTime.substring(2,6)} hs"
    }
    new_time
  }

  def receive = {

      case TweetNextGame(game, tweet, team_name) => {
        val date_format = new SimpleDateFormat("dd/MM/yyyy")
        val local_team = game("home_team")("full_name").as[String].replaceAll("\\s", "")
        val visitor_team = game("visitor_team")("full_name").as[String].replaceAll("\\s", "")
        val time = argentinaTime(game("status").as[String])
        var date = date_format.format(game("date").as[Date])
        if (next_day) {
          date = s"${(date.substring(0,2).toInt + 1).toString}${date.substring(2)}"
        }
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