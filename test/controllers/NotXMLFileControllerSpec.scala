package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.NotXMLFileView

class NotXMLFileControllerSpec extends SpecBase {

  "NotXMLFile Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.NotXMLFileController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[NotXMLFileView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
