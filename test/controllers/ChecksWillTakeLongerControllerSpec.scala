/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import pages.{ContactEmailPage, SecondContactEmailPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ChecksWillTakeLongerView

class ChecksWillTakeLongerControllerSpec extends SpecBase {

  val firstContactEmail: String  = "firstContact@email.com"
  val secondContactEmail: String = "secondContact@email.com"

  "ChecksWillTakeLonger Controller" - {

    "must return OK and the correct view for a GET with only a first contact email" in {

      val userAnswers = emptyUserAnswers.set(ContactEmailPage, firstContactEmail).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ChecksWillTakeLongerController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ChecksWillTakeLongerView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(firstContactEmail, None)(request, messages(application)).toString
        contentAsString(result) must not include secondContactEmail
      }
    }

    "must return OK and the correct view for a GET with a second contact email" in {

      val userAnswers = emptyUserAnswers
        .set(ContactEmailPage, firstContactEmail)
        .success
        .value
        .set(SecondContactEmailPage, secondContactEmail)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ChecksWillTakeLongerController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ChecksWillTakeLongerView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(firstContactEmail, Some(secondContactEmail))(request, messages(application)).toString
        contentAsString(result) must include(secondContactEmail)
      }
    }
  }
}
