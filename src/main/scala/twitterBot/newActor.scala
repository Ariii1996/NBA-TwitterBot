package twitterBot

import akka.actor.{Actor, ActorSystem}

import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.Timeout

import scala.concurrent.duration.DurationInt

case class HashtagReceive(hashtag: String)

class NewActor(system: ActorSystem) extends Actor {

  def receive = {

    case HashtagReceive(hashtag: String) => {

      println(s"llego bien el hashtag: ${hashtag}")

      implicit val actorSystem = system
      implicit val timeout: Timeout = 5.seconds

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "https://www.balldontlie.io/api/v1/players/237"))

      responseFuture.flatMap(Unmarshal(_).to[String]).map(res => println(res))
    }
  }
}
