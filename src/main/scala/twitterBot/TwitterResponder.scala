package twitterBot

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor

case class TweetIt(response: String, tweet: Tweet)

class TwitterResponder() extends Actor {

  val client = TwitterRestClient()

  def receive = {

      case TweetIt(response, tweet) => {
        val res = response.substring(0,10)
        client.createTweet(
          status=s"@${tweet.user.get.screen_name} Te dejo la informacion que buscabas: \n${res}",
          in_reply_to_status_id=Option(tweet.id)
        )
      }
  }
}