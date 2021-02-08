package web

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.HttpApp
import helpers.{ProcessRequest, WebRequest}

class WebBot(HashtagController: ActorRef, WebResponder: ActorRef) extends HttpApp {

  def routes() =
    pathSingleSlash {
      concat(
        get {
          getFromResource("home/index.html")
        },
        post {
          formFields("actions") { action  =>
            action match {
              case "Jugador" => {
                formFields("playerName", "playerSurname") { (name, surname) =>
                  completeWith(implicitly[ToResponseMarshaller[HttpEntity.Strict]]) { f =>
                    val request = new WebRequest((action, name, surname), f)
                    HashtagController ! ProcessRequest(request, WebResponder)
                  }
                }
              }
              case "Proximopartido" => {
                formFields("teamName") { teamName =>
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
      get{
        getFromResource("home/" + resource)
      }
    }
}