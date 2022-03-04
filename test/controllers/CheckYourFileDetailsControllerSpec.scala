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
import models.{MDR401, MessageSpecData, UserAnswers, ValidatedFileData}
import pages.ValidXMLPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.CheckYourFileDetailsViewModel
import views.html.CheckYourFileDetailsView
import viewmodels.govuk.summarylist._

class CheckYourFileDetailsControllerSpec extends SpecBase {

  "CheckYourFileDetails Controller" - {

    "must return OK and the correct view for a GET" in {

      val vfd: ValidatedFileData = ValidatedFileData("test.xml", MessageSpecData("GDC99999999", MDR401))
      val ua: UserAnswers        = emptyUserAnswers.set(ValidXMLPage, vfd).success.value
      val application            = applicationBuilder(userAnswers = Some(ua)).build()
      val action                 = routes.SendYourFileController.onPageLoad().url

      running(application) {
        val request = FakeRequest(GET, routes.CheckYourFileDetailsController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourFileDetailsView]

        val list = SummaryListViewModel(CheckYourFileDetailsViewModel.getSummaryRows(vfd)(messages(application)))
          .withoutBorders()
          .withCssClass("govuk-!-margin-bottom-0")

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, action)(request, messages(application)).toString
      }
    }
  }
}
