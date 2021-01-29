package twitterBot

import akka.actor.{Actor, ActorRef}
import com.danielasfregola.twitter4s.entities.Tweet

case class ManageTweet(tweet: Tweet)

class HashtagController(NBArequester: ActorRef) extends Actor {

  def receive = {

    case ManageTweet(tweet) => {

      val hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
      var hashtag = ""
      if (hashtags.size > 0) {
      hashtag = hashtags(0).text
      }

      NBArequester ! searchTeamNextGame(hashtag.toLowerCase().capitalize, tweet)
    }
  }
}
