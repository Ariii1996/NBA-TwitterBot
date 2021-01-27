package twitterBot

import akka.actor.{Actor, ActorRef}

import com.danielasfregola.twitter4s.entities.streaming.{StreamingMessage}
import com.danielasfregola.twitter4s.entities.{Tweet}
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}

case class ListenMentions()

class TwitterBot(newActor: ActorRef) extends Actor {

  val restClient = TwitterRestClient()
  val streamingClient = TwitterStreamingClient() //is the client to support stream
  // connections offered by the Twitter Streaming Api.

  val NBAAccount = Seq("NBAInformation6")

  def receive = {

    case ListenMentions => {

      def printTweet(tweet: Tweet) = {
        var hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
        var hashtag = ""
        if (hashtags.size > 0) {
          hashtag = hashtags(0).text
        }

        newActor ! HashtagReceive(hashtag)

        println(s"Hashtag: ${hashtag}")

        var user = tweet.user.get
        //println(s"Respondiendo la mencion (${tweet.id}) del usuario ${user.screen_name}")
      }

      streamingClient.filterStatuses(tracks=NBAAccount) {
        case tweet: Tweet => printTweet(tweet)
      }
    }
    case _ => println("Se recibio otra cosa")
  }
}



