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
          formFields("actions") { action  =>
            println(s"La accion buscada fue: $action")
            action match {
              case "Jugador" => {
                formFields("playerName", "playerSurname") { (name, surname) =>
                  println(name, surname)
                  completeWith(implicitly[ToResponseMarshaller[HttpEntity.Strict]]) { f =>
                    val request = new WebRequest((action, name, surname), f)
                    HashtagController ! ProcessRequest(request, WebResponder)
                  }
                }
              }
              case "Proximopartido" => {
                formFields("teamName") { teamName =>
                  println(teamName)
                  completeWith(implicitly[ToResponseMarshaller[HttpEntity.Strict]]) { f =>
                    val request = new WebRequest((action,teamName, ""), f)
                    HashtagController ! ProcessRequest(request, WebResponder)
                  }
                }
              }
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