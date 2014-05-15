package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action { implicit request =>
    println("METHOD: " + request.method)
    println("ORIGIN: " + request.headers)
    var ret = if (request.method == "OPTIONS") {
      Ok("")
    } else {
      Ok("""{"status" : "OK"}""").as("application/json")
    }
    ret.withHeaders(
      "Access-Control-Allow-Origin" -> "http://localhost:9000",
      "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS",
      "Access-Control-Allow-Headers" -> "X-PINGOTHER"
    )
  }

  def index2 = Action {
    Ok(views.html.index("test"))
  }

}