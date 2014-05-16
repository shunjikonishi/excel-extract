package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.io.File
import java.io.IOException
import java.util.Locale
import jp.co.flect.excel2canvas.NamedValueExtractor

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Excel extractor"))
    /*
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
    */
  }

  def upload = Action(parse.multipartFormData) { implicit request =>
    val (filename, file) = request.body.file("file").map { file =>
      val temp = File.createTempFile("tmp", file.filename)
      file.ref.moveTo(temp, true)
      (file.filename, temp)
    }.getOrElse(throw new IOException("Could not find uploaded file."))
    Form(tuple(
      "locale" -> nonEmptyText,
      "convertString" -> boolean,
      "includeFormula" -> boolean
    )).bindFromRequest.fold(
      hasErrors={ form =>
        throw new IllegalStateException()
      },
      success={ case (locale, convertString, includeFormula) =>
        val extractor = new NamedValueExtractor(new Locale(locale))
        extractor.setConvertString(convertString)
        extractor.setIncludeFormulaValue(includeFormula)
        val map = extractor.extract(file)
        Ok(NamedValueExtractor.toJson(map)).as("application/json;charset=utf-8")
      }
    )
  }

}