package twitterBot

import akka.actor.{Actor}

case class HashtagReceive(hashtag: String)

class NewActor() extends Actor {

  def receive = {
    case HashtagReceive(hashtag: String) => {
      println(s"llego bien el hashtag: ${hashtag}")
    }
  }
}




