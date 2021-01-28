package twitterBot

import akka.actor.{Actor, ActorSystem, ActorRef}
import com.danielasfregola.twitter4s.entities.Tweet
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

case class RequestInfo(hashtag: String, tweet: Tweet)

class NBArequester(system: ActorSystem, TwitterResponder: ActorRef) extends Actor {

  def receive = {

    case RequestInfo(hashtag, tweet) => {

      println(s"llego bien el hashtag: ${hashtag}")

      implicit val actorSystem = system
      implicit val timeout: Timeout = 5.seconds

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "https://www.balldontlie.io/api/v1/players/237"))

      responseFuture
        .flatMap(Unmarshal(_).to[String])
        .map(TwitterResponder ! TweetIt(_,tweet))
    }
  }
}
