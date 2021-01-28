package twitterBot

import akka.actor.{Actor, ActorRef}
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterStreamingClient

case class ListenMentions()

class TwitterBot(newActor: ActorRef) extends Actor {

  val streamingClient = TwitterStreamingClient() //is the client to support stream
  // connections offered by the Twitter Streaming Api.

  val NBAAccount = Seq("NBAInformation6")

  def receive = {

    case ListenMentions => {

      def printTweet(tweet: Tweet) = {
        val hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
        var hashtag = ""
        if (hashtags.size > 0) {
          hashtag = hashtags(0).text
        }

        newActor ! RequestInfo(hashtag, tweet)
      }

      streamingClient.filterStatuses(tracks=NBAAccount) {
        case tweet: Tweet => printTweet(tweet)
      }
    }
    case _ => println("Se recibio otra cosa")
  }
}



