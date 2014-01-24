package controllers

import play.api._
import play.api.libs.EventSource
import play.api.Play.current
import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import play.api.libs.iteratee._   //Import playframework iteratee api for streaming
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def helloworld = Action {
    //Ok means the response status is 200
    //The output can be simple a string
    //Ok("Hello world")
    //Or, a template
    Ok(views.html.helloworld("Hello World"))
  }
  
  lazy val clock: Enumerator[String] = {
    import java.util._
    import java.text._
    
    val dateFormat = new SimpleDateFormat("HH:mm:ss")
    
    Enumerator.generateM {
      Promise.timeout(Some(dateFormat.format(new Date)), 1 second)
    }
  }
  
  def realtimeClock = Action {
    
    /**
     * HTTP 1.1 chunk encoding enable the server to send a response whose length
     * is unknown; This gives the capability of keeping the connection alive
     * and streaming the response
     */
    Ok.chunked(clock through EventSource()).as("text/event-stream")
  }
  
  def liveClock = Action {
    Ok(views.html.liveclock())
  }

}