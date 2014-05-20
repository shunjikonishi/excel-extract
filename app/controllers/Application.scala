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
  }

  def options = Action { implicit request =>
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "POST, OPTIONS"
    )
  }
  def upload = Action(parse.multipartFormData) { implicit request =>
    val (filename, file) = request.body.file("file").map { file =>
      val temp = File.createTempFile("tmp", file.filename)
      file.ref.moveTo(temp, true)
      (file.filename, temp)
    }.getOrElse(throw new IOException("Could not find uploaded file."))
    Form(tuple(
      "language" -> optional(text),
      "convertString" -> optional(boolean),
      "includeFormula" -> optional(boolean)
    )).bindFromRequest.fold(
      hasErrors={ form =>
        throw new IllegalStateException()
      },
      success={ case (l, cs, f) =>
        val locale = l.map(new Locale(_)).getOrElse(Locale.getDefault)
        val convertString = cs.getOrElse(false)
        val includeFormula = f.getOrElse(false)
        val extractor = new NamedValueExtractor(locale)
        extractor.setConvertString(convertString)
        extractor.setIncludeFormulaValue(includeFormula)
        val map = extractor.extract(file)
        Ok(NamedValueExtractor.toJson(map))
          .as("application/json;charset=utf-8")
          .withHeaders(
            "Access-Control-Allow-Origin" -> "*",
            "Access-Control-Allow-Methods" -> "POST, OPTIONS"
          )
      }
    )
  }

}