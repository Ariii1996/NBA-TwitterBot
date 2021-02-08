package web

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import play.api.libs.json.JsValue
import helpers.{Request, Responder, WebRequest}

class WebResponder() extends Responder {

  def respondNextGame(game: JsValue, _request: Request, team_name: String): Unit = {

    val request = _request.asInstanceOf[WebRequest]

    val local_team = game("home_team")("full_name").as[String].replaceAll("\\s", "")
    val visitor_team = game("visitor_team")("full_name").as[String].replaceAll("\\s", "")
    val (date, time) = Utils.timeZoneChangeToBsAs(game("date").as[String], game("status").as[String])

    val htmlScreen = s"""
      <head>
        <link rel="stylesheet" href="resources/styles.css">
      </head>
      <h1 class="title"> Proximo partido de los $team_name es: </h1>
      <h2> $local_team vs $visitor_team </h2>
      <h2> Fecha: $date </h2>
      <h2> Horario: $time </h2>
    """
    request.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlScreen))
  }

  def respondPlayerStats(stats: Seq[Any], _request: Request): Unit = {

    val request = _request.asInstanceOf[WebRequest]
    val htmlScreen = s"""
      <head>
        <link rel="stylesheet" href="resources/styles.css">
      </head>
      <h1 class="title"> Datos del jugador ${stats.head} : </h1>
      <h2> Equipo: ${stats(1)} </h2>
      <h2> Posicion: ${Utils.positionParse(stats(2).toString)} </h2>
      <h2> Altura: ${stats(3)} m </h2>
      <h2> Peso: ${stats(4)} kg </h2>
      <h2> Puntos por partido: ${stats(5)} </h2>
      <h2> Asistencias por partido: ${stats(6)} </h2>
      <h2> Rebotes por partido: ${stats(7)} </h2>
      <h2> Bloqueos por partido: ${stats(8)} </h2>
      <h2> Robos por partido: ${stats(9)} </h2>
    """
    request.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlScreen))
  }

  def respondHelp(_request: Request): Unit = {
    val request = _request.asInstanceOf[WebRequest]
    println("respondHelp")
  }
  def respondError(_request: Request, message: String): Unit = {
    val request = _request.asInstanceOf[WebRequest]
    val htmlScreen = s"""
      <head>
        <link rel="stylesheet" href="resources/styles.css">
      </head>
      <h1 class="title"> $message </h1>
    """
    request.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlScreen))
  }
  def respondInternalError(_request: Request): Unit = {
    val request = _request.asInstanceOf[WebRequest]
    val htmlScreen = """
      <head>
        <link rel="stylesheet" href="resources/styles.css">
      </head>
      <h1 class="title"> Lo sentimos, ocurrió un error en nuestros servidores</h1>
    """
    request.complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, htmlScreen))
  }
  def respondWelcome(_request: Request): Unit = {
    val request = _request.asInstanceOf[WebRequest]
    println("respondWelcome")
  }
}
