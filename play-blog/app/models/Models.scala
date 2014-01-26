package models

import play.api.db.slick.Config.driver.simple._

case class BlogPost(id: Option[Long], title: String, body: String)

object BlogPosts extends Table[BlogPost]("blog_posts") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title", O.NotNull)
  def body = column[String]("body", O.NotNull)

  def * = id.? ~ title ~ body <> (BlogPost.apply _, BlogPost.unapply _)

  def list(implicit s: Session) = {
    val q = for {
      p <- BlogPosts
    } yield p
    q.list
  }

  def get(id:Long)(implicit s:Session) = {
    val q = for ( p <- BlogPosts if p.id===id) yield p
    q.first
  }
  
  def size(implicit s: Session):Int = BlogPosts.list.length
}

case class BlogComment(id: Option[Long], blogId:Long, comment:String )

object BlogComments extends Table[BlogComment]("blog_comments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def blogId = column[Long]("blog_id", O.NotNull)
  def comment = column[String]("comment", O.NotNull)
  
  def blogPost = foreignKey("BLOGPOST_FK", blogId, BlogPosts)(_.id)
  
  def * = id.? ~ blogId ~ comment <> (BlogComment.apply _, BlogComment.unapply _)
  
  def list(blogPostId:Long)(implicit s:Session) = {
    val q = for(comment <- BlogComments if comment.blogId === blogPostId) yield comment
    q.list
  }
}