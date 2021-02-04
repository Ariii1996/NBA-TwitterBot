package twitterBot

import akka.actor.{Actor, ActorRef, Status}
import com.danielasfregola.twitter4s.entities.Tweet
import play.api.libs.json.JsValue

case class ManageTweet(request: Tweet)
case class SendNextGame(game: JsValue, request: Tweet, team_name: String)
case class SendPlayerStats(player: Seq[Any], request: Tweet)
case class SendError(request: Tweet, message: String)

class HashtagController(NBArequester: ActorRef, TwitterResponder: ActorRef) extends Actor {

  var tweet: Tweet = null

  def receive: Receive = {
    case ManageTweet(tweet) => {
      this.tweet = tweet
      val hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
      var (action, firstHashtag, secondHashtag) = ("","","")
      if(hashtags.nonEmpty){
        action = hashtags.head.text.toLowerCase().capitalize
        if(hashtags.size >= 2) firstHashtag = hashtags(1).text.toLowerCase().capitalize
        if(hashtags.size >= 3) secondHashtag = hashtags(2).text.toLowerCase().capitalize

        action match {
          case "Jugador" => {
            if(firstHashtag != "" & secondHashtag != "")
              NBArequester ! searchPlayerStats(firstHashtag, secondHashtag, tweet)
            else TwitterResponder ! TweetError(tweet, "No se introdujo el nombre o el apellido del jugador")
          }
          case "Proximopartido" => {
            if(firstHashtag != "") NBArequester ! searchTeamNextGame(firstHashtag, tweet)
            else TwitterResponder ! TweetError(tweet, "No se introdujo el nombre del equipo")
          }
          case "Ayuda" => TwitterResponder ! TweetHelp(tweet)
          case _ => TwitterResponder ! TweetError(tweet, "No se introdujo ninguna acción")
        }
      }else TwitterResponder ! TweetWelcome(tweet)
    }
    case SendNextGame(game, request, team_name) => {
      TwitterResponder ! TweetNextGame(game, request, team_name)
    }
    case SendPlayerStats(stats, request) => {
      TwitterResponder ! TweetPlayerStats(stats, request)
    }
    case SendError(request, message) => {
      TwitterResponder ! TweetError(request, message)
    }
    case Status.Failure(error) => {
      println(s"Hubo un error al hacer la request $error")
      TwitterResponder ! TweetInternalError(tweet)
    }
  }
}
