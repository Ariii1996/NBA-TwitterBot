package twitterBot

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import twitterBot.{NewActor}
import twitterBot.{ListenMentions, TwitterBot}


object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val newActor = system.actorOf(Props(classOf[NewActor], system), "newActor")
    val twitterBot = system.actorOf(Props(classOf[TwitterBot], newActor), "twitterBot")

    twitterBot ! ListenMentions
  }
}