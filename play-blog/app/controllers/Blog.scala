package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.db.slick._
import play.api.mvc._
import play.api.Logger
import models._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.json._
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object Blog extends Controller {
  val postForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "title" -> nonEmptyText(),
      "body" -> nonEmptyText(10, 140)
    )(BlogPost.apply)(BlogPost.unapply)
  )
  
  val commentForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "blog_id" -> longNumber,
      "comment" -> nonEmptyText()      
    )(BlogComment.apply)(BlogComment.unapply)
  )
  
  val (commentOut, commentChannel) = Concurrent.broadcast[JsValue]
  
  def index = DBAction { implicit rs =>
    val posts = BlogPosts.list
    Ok(views.html.index(postForm, posts))
  }
  
  def post() = DBAction {implicit rs =>
    postForm.bindFromRequest.fold (
      formWithErrors => Redirect(routes.Blog.index()).flashing(
          Flash(formWithErrors.data) + ("error" -> "Form validation errors")),
      post => {
        BlogPosts.insert(post)
        Redirect(routes.Blog.index())
      }
    )
  }
  
  def comments(postId:Long) = DBAction {implicit rs =>
    val post = BlogPosts.get(postId)
    val comments = BlogComments.list(postId)
    Ok(views.html.comments(commentForm, comments, post))
  }
  
  implicit val commentFormatter = Json.format[BlogComment]
  
  def saveComment(postId:Long) = DBAction {implicit rs =>
    commentForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Blog.comments(postId)),
      comment => {
        BlogComments.insert(comment)
        commentChannel.push(Json.toJson(comment))
        Redirect(routes.Blog.comments(postId))
      }
    )
  }
  
  def commentFeed(postId:Long) = Action { request =>
    def filter = Enumeratee.filter[JsValue] {
      json:JsValue => (json \ "blogId").as[Long] == postId
    }
    Ok.chunked(
        commentOut through 
        filter through
        EventSource()).as("text/event-stream")
    
  }

}
