package twitterBot

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.TwitterRestClient
import akka.actor.Actor
import play.api.libs.json.JsValue

class TwitterResponder() extends Responder {

  val Utils = new Utils
  val client = TwitterRestClient()

  override def respondNextGame(game: JsValue, _tweet: Request, team_name: String): Unit = {

    val tweet = _tweet.asInstanceOf[TwitterRequest]

    val local_team = game("home_team")("full_name").as[String].replaceAll("\\s", "")
    val visitor_team = game("visitor_team")("full_name").as[String].replaceAll("\\s", "")
    val (date, time) = Utils.timeZoneChangeToBsAs(game("date").as[String], game("status").as[String])
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} El próximo partido de los #${team_name} es:" +
        s"\n\n#$local_team vs #$visitor_team " +
        s"\nFecha: $date" +
        s"\nHorario: $time (Arg)" ,
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
  override def respondPlayerStats(stats: Seq[Any], _tweet: Request): Unit = {

    val tweet = _tweet.asInstanceOf[TwitterRequest]
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} Datos del jugador #${stats(0)} :" +
        s"\n\nEquipo: #${stats(1)}" +
        s"\nPosicion: ${Utils.positionParse(stats(2).toString)}" +
        s"\nAltura: ${stats(3)} m" +
        s"\nPeso: ${stats(4)} kg" +
        s"\nPuntos por partido: ${stats(5)}" +
        s"\nAsistencias por partido: ${stats(6)}" +
        s"\nRebotes por partido: ${stats(7)}" +
        s"\nBloqueos por partido: ${stats(8)}" +
        s"\nRobos por partido: ${stats(9)}",
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
  override def respondHelp(_tweet: Request): Unit = {

    val tweet = _tweet.asInstanceOf[TwitterRequest]
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} " +
        s"\n\nInstrucciones de uso: " +
        s"\n\n - Información del proximo partido de tu equipo: " +
        s"\n@NBAInformation6 #ProximoPartido #NombreDeTuEquipo" +
        s"\n\n- Información sobre tu jugador: " +
        s"\n@NBAInformation6 #Jugador #NombreDeTuJugador #ApellidoDeTuJugador",
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
  override def respondError(_tweet: Request, message: String): Unit = {
    val tweet = _tweet.asInstanceOf[TwitterRequest]
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} $message" +
        s"\n\nPara más ayuda: " +
        s"\n@NBAInformation6 #Ayuda",
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
  override def respondInternalError(_tweet: Request): Unit = {
    val tweet = _tweet.asInstanceOf[TwitterRequest]
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} Lo sentimos, ocurrió un error en nuestros servidores",
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
  override def respondWelcome(_tweet: Request): Unit = {
    val tweet = _tweet.asInstanceOf[TwitterRequest]
    client.createTweet(
      status=s"@${tweet.getTweet().user.get.screen_name} " +
        s"Hola! Si querés ver que iformación te puedo dar twittea: " +
        s"\n\n@NBAInformation6 #Ayuda",
      in_reply_to_status_id=Option(tweet.getTweet().id)
    )
  }
}
