package twitterBot
import akka.http.scaladsl.model.HttpEntity
import com.danielasfregola.twitter4s.entities.Tweet

abstract class Request {
  def getAction(): (String, String, String)
}

class TwitterRequest(tweet: Tweet) extends Request {

  override def getAction() : (String, String, String) = {

    val hashtags = tweet.entities.map(_.hashtags).getOrElse(List.empty)
    var (action, firstHashtag, secondHashtag) = ("","","")
    if(hashtags.nonEmpty) {
      action = hashtags.head.text.toLowerCase().capitalize
      if (hashtags.size >= 2) firstHashtag = hashtags(1).text.toLowerCase().capitalize
      if (hashtags.size >= 3) secondHashtag = hashtags(2).text.toLowerCase().capitalize
      (action, firstHashtag, secondHashtag)
    }
    else {
      ("Welcome", "", "")
    }
  }

  def getTweet(): Tweet = tweet
}

class WebRequest(value: String, complete: HttpEntity.Strict => Unit) extends Request{

  override def getAction(): (String, String, String) = {
    println(value)
    ("Jugador", "Lebron", "James")
  }
}

