import akka.actor.{ActorSystem, Props}
import twitter.{ListenMentions, TwitterBot, TwitterResponder}
import helpers.{NBAController, NBArequester}
import web.{WebBot, WebResponder}

object Main {

  def main(args: Array[String]): Unit = {

    val system = ActorSystem()

    val TwitterResponder = system.actorOf(Props(classOf[TwitterResponder]), "TwitterResponder")
    val NBArequester = system.actorOf(Props(classOf[NBArequester], system), "NBArequester")
    val HashtagController = system.actorOf(Props(classOf[NBAController], NBArequester), "HashtagController")
    val TwitterBot = system.actorOf(Props(classOf[TwitterBot], HashtagController, TwitterResponder), "helpers")
    val WebResponder = system.actorOf(Props(classOf[WebResponder]), "webResponder")
    val WebBot = new WebBot(HashtagController, WebResponder)

    TwitterBot ! ListenMentions
    WebBot.startServer("localhost", 3000)
  }
}
