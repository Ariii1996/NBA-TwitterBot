package twitterBot

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling._

class WebBot(HashtagController: ActorRef, WebResponder: ActorRef) extends HttpApp {

  def routes() =
    pathSingleSlash {
      concat(
        get {
          getFromResource("home/index.html")
        },
        post {
          formFields("query") { value  =>
            println(s"Buscando nombre $value")
            completeWith(implicitly[ToResponseMarshaller[HttpEntity.Strict]]) { f =>
              val request = new WebRequest(value, f)
              HashtagController ! ProcessRequest(request, WebResponder)
            }
          }
        }
      )
    } ~
    path("home" / Remaining) { resource =>
      // Ruta necesaria para los .css y .png
      get{
        getFromResource("home/" + resource)
      }
    }
}