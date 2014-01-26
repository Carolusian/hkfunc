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
  
  def saveComment(postId:Long) = DBAction {implicit rs =>
    commentForm.bindFromRequest.fold(
      formWithErrors => Redirect(routes.Blog.comments(postId)),
      comment => {
        BlogComments.insert(comment)
        Redirect(routes.Blog.comments(postId))
      }
    )
  }

}
