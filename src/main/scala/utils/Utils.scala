package utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import java.util.Locale

class Utils() {

  def timeZoneChangeToBsAs(date: String, status: String): (String, String) = {

    status match {
      case "1st Qtr" | "2nd Qtr" | "3rd Qtr" | "4th Qtr" => {
        ("Se esta jugando", "Ahora mismo")
      }
      case "Halftime" => {
        ("Es el entretiempo", "Ahora mismo")
      }
      case _ => {
        val dateString = date.substring(0, 10)
        var timeString = ""
        if (status(1) == ':') timeString = "0" + timeString.concat(status.substring(0, 4))
        else timeString = timeString.concat(status.substring(0, 5))
        val dateAndTime = dateString + " " + timeString + " PM"
        val sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US)
        val localZone = LocalDateTime.parse(dateAndTime, sourceFormatter)
        val ESTzone = localZone.atZone(ZoneId.of("America/New_York"))
        val ARGzone = ESTzone.withZoneSameInstant(ZoneId.of("America/Buenos_Aires")).toString
        (ARGzone.substring(8, 10) + "/" + ARGzone.substring(5, 7) + "/" + ARGzone.substring(0, 4), ARGzone.substring(11, 16) + " hs (Arg)")
      }
    }
  }

  def positionParse(position: String): String ={
    position match {
      case "F" => "Alero"
      case "G" => "Base/Escolta"
      case "G-F" => "Escolta/Alero"
      case "F-G" => "Base/Alero"
      case "C" => "Pivot"
      case "F-C" => "Ala-pivot"
      case _ => "Indefinido"
    }
  }
}