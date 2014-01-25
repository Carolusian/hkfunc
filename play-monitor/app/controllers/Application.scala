package controllers

import play.api.mvc._
import play.api.libs._
import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import play.api.libs.iteratee.Enumerator

import org.hyperic.sigar.Cpu
import org.hyperic.sigar.Sigar

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def upload() = Action {
    Ok(views.html.upload());
  }
  
  def uploadFile() = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>
      import java.io.File
      val filename = file.filename
      file.ref.moveTo(new File(s"D:/$filename"), true)
      Redirect(routes.Application.upload)
    }.getOrElse {
      Redirect(routes.Application.upload)
    }
  }
  
  lazy val cpuUsageStream: Enumerator[String] = Enumerator.generateM {
    val sigar = new Sigar()
    val cpu = sigar.getCpuPerc()
    Promise.timeout(
      Some(1.0 - cpu.getIdle() toString),
      100 millisecond)
  }
  
  def monitor = Action {
    Ok.chunked(cpuUsageStream &> EventSource()).as("text/event-stream")
  }
}