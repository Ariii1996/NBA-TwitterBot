package twitterBot

import akka.actor.{ActorSystem, Props}


object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val TwitterResponder = system.actorOf(Props(classOf[TwitterResponder]), "TwitterResponder")
    val NBArequester = system.actorOf(Props(classOf[NBArequester], system, TwitterResponder), "NBArequester")
    val HashtagController = system.actorOf(Props(classOf[HashtagController], NBArequester), "HashtagController")
    val TwitterBot = system.actorOf(Props(classOf[TwitterBot], HashtagController), "twitterBot")

    TwitterBot ! ListenMentions
  }
}