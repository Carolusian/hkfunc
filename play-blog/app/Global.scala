import play.api._
import models._
import play.api.db.slick._
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._

object Global extends GlobalSettings {
  override def onStart(app:Application) = {
    InitialData.insert()
  }
}

object InitialData {
  def insert() = {
    DB.withSession { implicit s:Session =>
      if(BlogPosts.size == 0) {
        Seq(
          BlogPost(Option(1L), "This is the first Post", "Some body text"),
          BlogPost(Option(2L), "This is the second", "Some body text for the second post")
        ).foreach(BlogPosts.insert)
      }
    }
  }
}