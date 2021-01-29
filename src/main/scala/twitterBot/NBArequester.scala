package twitterBot

import akka.actor.{Actor, ActorSystem, ActorRef}
import com.danielasfregola.twitter4s.entities.Tweet
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import play.api.libs.json._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout
import scala.util.{ Success, Failure }

import scala.concurrent.duration.DurationInt

case class RequestInfo(hashtag: String, tweet: Tweet)

class NBArequester(system: ActorSystem, TwitterResponder: ActorRef) extends Actor {

  def receive = {

    case RequestInfo(hashtag, tweet) => {

      println(s"llego bien el hashtag: ${hashtag}")

      implicit val actorSystem = system
      implicit val timeout: Timeout = 5.seconds

      val responseFuture = Http().singleRequest(HttpRequest(uri = "https://www.balldontlie.io/api/v1/players/237"))
        .flatMap { res =>
          Unmarshal(res).to[String].map { data =>
            Json.parse(data)
          }
        }

      responseFuture.onComplete {
        case Success(json) =>
          val id = (json \ "id" ).get
          TwitterResponder ! TweetIt(id.toString(), tweet)
        case Failure(e) =>
          println(s"Failure: $e")
      }


    }
  }
}
