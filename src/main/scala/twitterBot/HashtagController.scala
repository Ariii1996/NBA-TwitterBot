package twitterBot

import akka.actor.{Actor, ActorRef}
import com.danielasfregola.twitter4s.entities.Tweet

case class ManageTweet(tweet: Tweet)

class HashtagController(NBArequester: ActorRef) extends Actor {

  def receive: Receive = {
    case ManageTweet(tweet) => {

      val hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
      var (action, firstHashtag, secondHashtag) = ("","","")
      if(hashtags.nonEmpty){
        action = hashtags.head.text.toLowerCase().capitalize
        if(hashtags.size >= 2) firstHashtag = hashtags(1).text.toLowerCase().capitalize
        if(hashtags.size >= 3) secondHashtag = hashtags(2).text.toLowerCase().capitalize
      }
      action match {
        case "Jugador" => {
          if(firstHashtag != "" & secondHashtag != "")
            NBArequester ! searchPlayerStats(firstHashtag, secondHashtag, tweet)
        }
        case "Proximopartido" => {
          if(firstHashtag != "") NBArequester ! searchTeamNextGame(firstHashtag, tweet)
        }
        //case "Comandos" => NBArequester !
        case _ => println("No hubo hashtag de acción")
      }
    }
  }
}
