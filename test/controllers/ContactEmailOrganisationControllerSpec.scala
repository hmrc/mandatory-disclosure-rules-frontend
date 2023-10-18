/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.{SpecBase, TestValues}
import forms.ContactEmailOrganisationFormProvider
import models.{AffinityType, NormalMode, UserAnswers}
import navigation.{ContactDetailsNavigator, FakeContactDetailsNavigator}
import org.mockito.ArgumentMatchers.any
import pages.{ContactEmailPage, ContactNamePage}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import views.html.ContactEmailOrganisationView

import scala.concurrent.Future

class ContactEmailOrganisationControllerSpec extends SpecBase {

  override def onwardRoute = Call("GET", "/foo")

  val formProvider = new ContactEmailOrganisationFormProvider()
  val form         = formProvider()
  val organisation = AffinityType(Organisation)
  val name         = "name"

  lazy val contactEmailRoute = routes.ContactEmailOrganisationController.onPageLoad().url
  lazy val havePhonelRoute   = routes.HaveTelephoneController.onPageLoad(organisation).url

  "OrgansisationContactEmail Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(ContactNamePage, name)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contactEmailRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ContactEmailOrganisationView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, name, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(ContactNamePage, name)
        .success
        .value
        .set(ContactEmailPage, TestValues.userAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, contactEmailRoute)

        val view = application.injector.instanceOf[ContactEmailOrganisationView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(TestValues.userAnswer), name, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[ContactDetailsNavigator].toInstance(new FakeContactDetailsNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, contactEmailRoute)
            .withFormUrlEncodedBody((TestValues.inputValue, TestValues.emailId))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = UserAnswers(userAnswersId)
        .set(ContactNamePage, name)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, contactEmailRoute)
            .withFormUrlEncodedBody((TestValues.inputValue, ""))

        val boundForm = form.bind(Map(TestValues.inputValue -> ""))

        val view = application.injector.instanceOf[ContactEmailOrganisationView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, name, NormalMode)(request, messages(application)).toString
      }
    }

  }
}
