import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._
import play.test.WithApplication


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
//@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {




/*
  "Application" should {

    "run in a server" in {
      running(TestServer(3333)) {

        await(WS.url("http://localhost:3333").get).status must equalTo(OK)

      }
    }
*/
  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("shouts out")
    }
  }


/*    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("shouts out")
    }*/

}
