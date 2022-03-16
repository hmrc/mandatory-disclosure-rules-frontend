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
import models.ConversationId
import models.fileDetails.{Accepted, FileDetails}
import pages.FileDetailsPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileStatusViewModel
import views.html.FilePassedChecksView

import java.time.LocalDateTime

class FilePassedChecksControllerSpec extends SpecBase {

  "FilePassedChecks Controller" - {

    "must return OK and the correct view for a GET" in {

      val fileDetails = FileDetails(
        "name",
        "messageRefId",
        LocalDateTime.parse("2022-01-01T10:30:00.000"),
        LocalDateTime.parse("2022-01-01T10:30:00.000"),
        Accepted,
        ConversationId("conversationId")
      )

      val userAnswers = emptyUserAnswers
        .set(FileDetailsPage, fileDetails)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {

        val fileSummaryList = FileStatusViewModel.createFileSummary(fileDetails.name, fileDetails.status)(messages(application))
        val action          = routes.FileReceivedController.onPageLoad().url
        val request         = FakeRequest(GET, routes.FilePassedChecksController.onPageLoad().url)
        val result          = route(application, request).value
        val view            = application.injector.instanceOf[FilePassedChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action)(request, messages(application)).toString
      }
    }
  }
}
