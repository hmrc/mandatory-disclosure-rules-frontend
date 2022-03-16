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
import models.{ConversationId, MDR401, MessageSpecData, UserAnswers, ValidatedFileData}
import pages.ValidXMLPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.{CheckYourFileDetailsViewModel, FileStatusViewModel}
import views.html.FilePassedChecksView

class FilePassedChecksControllerSpec extends SpecBase {

  "FilePassedChecks Controller" - {

    "must return OK and the correct view for a GET" in {

      val vfd: ValidatedFileData = ValidatedFileData("test.xml", MessageSpecData("GDC99999999", MDR401))
      val ua: UserAnswers        = emptyUserAnswers.set(ValidXMLPage, vfd).success.value
      val conversationId         = ConversationId("123")
      val application            = applicationBuilder(userAnswers = Some(ua)).build()
      val action                 = routes.FileReceivedController.onPageLoad(conversationId).url

      running(application) {

        val fileSummaryList = FileStatusViewModel.createFileSummary(vfd.fileName, "Accepted")(messages(application))
        val request         = FakeRequest(GET, routes.FilePassedChecksController.onPageLoad(conversationId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[FilePassedChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action)(request, messages(application)).toString
      }
    }
  }
}
