package twitterBot

import play.api.libs.json.JsValue

class WebResponder() extends Responder {

  def respondNextGame(game: JsValue, _ɼequest: Request, team_name: String): Unit = {
    println(game)
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondNextGame")
  }
  def respondPlayerStats(stats: Seq[Any], _ɼequest: Request): Unit = {
    println(stats)
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondPlayerStats")
  }
  def respondHelp(_ɼequest: Request): Unit = {
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondHelp")
  }
  def respondError(_ɼequest: Request, message: String): Unit = {
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondError")
  }
  def respondInternalError(_ɼequest: Request): Unit = {
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondInternalError")
  }
  def respondWelcome(_ɼequest: Request): Unit = {
    val request = _ɼequest.asInstanceOf[WebRequest]
    println("respondWelcome")
  }
}
