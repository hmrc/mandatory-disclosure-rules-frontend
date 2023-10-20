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
import models.fileDetails.FileStatus
import pages.{ConversationIdPage, ValidXMLPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.FileCheckViewModel
import views.html.{FilePassedChecksView, ThereIsAProblemView}

class FilePassedChecksControllerSpec extends SpecBase {

  "FilePassedChecks Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(ValidXMLPage, TestValues.validatedFileData)
        .success
        .value
        .set(ConversationIdPage, TestValues.conversationId)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {

        val fileSummaryList = FileCheckViewModel.createFileSummary(TestValues.validatedFileData.fileName, FileStatus.accepted)(messages(application))
        val action          = routes.FileReceivedController.onPageLoadSlow(TestValues.conversationId).url
        val request         = FakeRequest(GET, routes.FilePassedChecksController.onPageLoad().url)
        val result          = route(application, request).value
        val view            = application.injector.instanceOf[FilePassedChecksView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(fileSummaryList, action)(request, messages(application)).toString
      }
    }

    "must throw an internal server error when no file data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {

        val request = FakeRequest(GET, routes.FilePassedChecksController.onPageLoad().url)
        val result  = route(application, request).value
        val view    = application.injector.instanceOf[ThereIsAProblemView]

        status(result) mustEqual INTERNAL_SERVER_ERROR
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }
  }
}
